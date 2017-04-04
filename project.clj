(defproject lava-bear "1.0.0"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.494"]
                 [org.omcljs/om "1.0.0-alpha48"]
                 [com.stuartsierra/component "0.3.1"]
                 [navis/untangled-client "0.7.0"]
                 [bidi "1.25.0"]
                 [http-kit "2.1.18"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.1.5"]
                 [com.taoensso/timbre "4.1.4"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles {:dev {:source-paths ["dev/server"]

                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [binaryage/devtools "0.9.2"]]

                   :plugins [[lein-figwheel "0.5.9"]]

                   :repl-options {:init-ns user
                                  :port 4000}

                   :cljsbuild {:builds [{:id "dev"
                                         :source-paths ["dev/client" "src/client"]
                                         :figwheel true
                                         :compiler {:main "user"
                                                    :asset-path "js/compiled/dev"
                                                    :output-to "resources/public/js/compiled/app.js"
                                                    :output-dir "resources/public/js/compiled/dev"
                                                    :recompile-dependents true
                                                    :optimizations :none}}]}

                   :figwheel {:repl false
                              :nrepl-port 5000
                              :css-dirs ["resources/public/css"]}}})
