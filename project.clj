(defproject degasolv/degasolv "2.3.0-SNAPSHOT"
  :description "Democratize dependency management."
  :url "http://github.com/djhaskin987/degasolv"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases false}]]

  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.flatland/ordered "1.5.9"]
                 [com.github.clj-easy/graal-build-time "1.0.5"]
                 [clj-http "3.12.0"]]
                 ;;[org.martinklepsch/clj-http-lite "0.4.3"]]
  :plugins [[lein-licenses "0.2.2"]
            [lein-print "0.1.0"]]

  :java-source-paths ["src/java"
                      "test/java"]
  :junit ["test/java"]
  :source-paths ["src"]

  :global-vars {*warn-on-reflection* true}

  :test-selectors
  {:unit-tests :unit-tests}
  :profiles {:dev {:source-paths ["src" "cli-src"]
                   :managed-dependencies
                   [[clj-http "3.12.0"]
                    [clj-tuple "0.2.2"]]
                   :dependencies [;; extra deps
                                  ;;[org.clojure/core.specs.alpha "0.2.56"]
                                  [clj-wiremock "0.3.0"]
                                  [pjstadig/humane-test-output "0.9.0"]
                                  [org.clojure/core.match "0.3.0-alpha5"]
                                            ;; normal deps
                                  [org.clojure/clojure "1.10.3"]
                                  [org.flatland/ordered "1.5.9"]
                                  [clj-http "3.12.0"]
                                  ;;[org.martinklepsch/clj-http-lite "0.4.3"]
                                            ;; cli deps
                                  [cheshire "5.9.0"]
                                  [com.velisco/tagged "0.5.0"]
                                  [org.clojure/tools.cli "0.3.5"]
                                  [serovers "1.6.2"]]
                   :plugins [[test2junit "1.4.2"]]
                   :test2junit-output-dir "target/test-results"
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}
             :uberjar {:aot :all
                                 ;; old aot list
                                 ;; [
                                 ;;       degasolv.pkgsys.core
                                 ;;       degasolv.pkgsys.apt
                                 ;;       degasolv.pkgsys.subproc
                                 ;;       degasolv.pkgsys.git
                                 ;;       degasolv.util
                                 ;;       degasolv.resolver
                                 ;;       degasolv.cli
                                 ;;       ]
                       :main degasolv.cli
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                       :source-paths ["src" "cli-src"]
                       :dependencies [;; normal deps
                                      [org.clojure/clojure "1.10.3"]
                                      [org.flatland/ordered "1.5.9"]
                                      [clj-http "3.12.0"]
                                      ;;[org.martinklepsch/clj-http-lite "0.4.3"]
                                                ;; cli deps
                                      [cheshire "5.9.0"]
                                      [com.velisco/tagged "0.5.0"]
                                      [org.clojure/tools.cli "0.3.5"]
                                      [serovers "1.6.2"]
                                                ;; uberjar deps
                                      [borkdude/clj-reflector-graal-java11-fix "0.0.1-graalvm-20.1.0"]]}}
  :target-path "target/%s/")
