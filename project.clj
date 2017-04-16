(defproject lava-bear "1.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.521"]
                 [org.omcljs/om "1.0.0-alpha48"]
                 [com.stuartsierra/component "0.3.1"]
                 [navis/untangled-client "0.7.0"]
                 [bidi "1.25.0"]
                 [http-kit "2.1.18"]
                 [hiccup "1.0.5"]
                 [garden "1.3.2"]
                 [ring/ring-defaults "0.1.5"]
                 [com.taoensso/timbre "4.1.4"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "resources/public/css/compiled"
                                    "target"
                                    "figwheel_server.log"
                                    ".lein-repl-history"]

  :min-lein-version "2.0.0"

  :omit-source true

  :profiles {:dev {:source-paths ["dev" "src"]

                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [binaryage/devtools "0.9.2"]]

                   :plugins [[lein-figwheel "0.5.9"]
                             [lein-garden "0.2.6"]]

                   :repl-options {:init-ns server.main
                                  :port 4000}

                   :cljsbuild {:builds [{:id "dev"
                                         :source-paths ["dev" "src"]
                                         :figwheel {:on-jsload "client.main/reload"}
                                         :compiler {:main "client.main"
                                                    :asset-path "js/compiled/dev"
                                                    :output-to "resources/public/js/compiled/app.js"
                                                    :output-dir "resources/public/js/compiled/dev"
                                                    :recompile-dependents true
                                                    :optimizations :none}}]}

                   :garden {:builds [{:id "dev"
                                      :source-paths ["src"]
                                      :stylesheet styles.main/main
                                      :compiler {:output-to "resources/public/css/compiled/app.css"
                                                 :pretty-print? true}}]}

                   :figwheel {:repl false
                              :nrepl-port 5000
                              :css-dirs ["resources/public/css"]}}

             :uberjar {:source-paths ["prod" "src"]

                       :uberjar-name "lava-bear-standalone.jar"

                       :aot :all

                       :main server.main

                       :plugins  [[lein-cljsbuild "1.1.5"]
                                  [lein-garden "0.2.6"]]

                       :hooks [leiningen.cljsbuild
                               leiningen.garden]

                       :cljsbuild {:builds [{:id "prod"
                                             :source-paths ["prod" "src"]
                                             :compiler {:main "client.main"
                                                        :asset-path "js/compiled/prod"
                                                        :output-to "resources/public/js/compiled/app.js"
                                                        :output-dir "resources/public/js/compiled/prod"
                                                        :recompile-dependents true
                                                        :optimizations :advanced}}]}

                       :garden {:builds [{:id "prod"
                                          :source-paths ["src"]
                                          :stylesheet styles.main/main
                                          :compiler {:output-to "resources/public/css/compiled/app.css"
                                                     :pretty-print? false}}]}}})
