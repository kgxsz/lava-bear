(defproject lava-bear "1.0.0"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.494"]
                 [org.omcljs/om "1.0.0-alpha48"]
                 [navis/untangled-client "0.7.0"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["dev/client" "src/client"]
                        :figwheel true
                        :compiler {:main "cljs.user"
                                   :asset-path "js/compiled/dev"
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/dev"
                                   :recompile-dependents true
                                   :optimizations :none}}]}

  :profiles {:dev {:source-paths ["dev/server"]
                   :dependencies [[figwheel-sidecar "0.5.9"]
                                  [binaryage/devtools "0.9.2"]]
                   :repl-options {:init-ns user
                                  :port 4000}}})
