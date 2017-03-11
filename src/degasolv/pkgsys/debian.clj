(ns degasolv.pkgsys.debian
  "Namespace containing functions related to the debian package system."
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [degasolv.util :refer :all]
            [degasolv.resolver :as r :refer :all])
  (:import (java.util.zip GZIPInputStream)))

; In case I change the zip input streamer later
(defn ->zip-input-stream
  [is]
  (GZIPInputStream. is))

(defn deb-to-degasolv-requirement
  [s]
  (as-> s it
        (string/replace it #"[ ()]" "")
        (string/replace it #"<<" "<")
        (string/replace it #">>" ">")
        (string/split it #",")
        (into [] it)))

(defn group-pkg-lines
  [lines]
  (as-> lines it
        (partition-by
          #(re-matches #"^Package:.*$" %)
          it)
        (partition 2 it)
        (map #(apply concat %) it)))

(defn lines-to-map
  [lines]
  (as-> lines it
        (map
          (fn [line]
            (let [[_ k v] (re-matches #"^([^:]+): +(.*)$" line)]
              [(keyword
                (string/lower-case k))
              v]))
          it)
        (into {} it)))



(defn convert-pkg-requirements
  [pkg]
  (let [deps (:depends pkg)]
    (if deps
      (assoc
        pkg
        :depends
        (deb-to-degasolv-requirement
          deps)))))

(defn add-pkg-location
  [pkg url]
  (assoc pkg
         :location
         (string/replace
           (str url "/" (:filename pkg))
           #"/+"
           "/")))

(defn apt-repo
  [url info]
  (as-> info it
        (string/split-lines it)
        (filter
          #(re-matches #"^(Package|Depends|Filename):.*" %)
          it)
        (group-pkg-lines it)
        (map
          lines-to-map
          it)
        (map
          (fn each-package
            [pkg]
            (as->
              pkg each
              (convert-pkg-requirements each)
              (add-pkg-location each url)
              (->PackageInfo
                (:package each)
                (:version each)
                (:location each)
                (:depends each))))
          it)
        (reduce
          (fn [c v]
            (if (not (get c (:id v)))
              (assoc c (:id v) [v])
              (update-in
                c
                [(:id v)] conj v)))
          {}
          it)))

(defn slurp-apt-repo
  [repospec aggregator]
  (let [[pkgtype url dist & pools]
             (string/split repospec #" +")]
    (aggregator
      (map
        (fn each-pool
          [pool]
          (as-> pool it
                (string/join
                  "/"
                  [url
                   "dists"
                   dist
                   it
                   pkgtype
                   "Packages.gz"])
                (with-open
                  [in
                   (->zip-input-stream
                     (io/input-stream it))]
                  (slurp in))
                (apt-repo url it)))
      pools))))
