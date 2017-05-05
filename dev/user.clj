(ns user
  (:require [server.system :refer [system]]
            [clojure.tools.namespace.repl :as repl]
            [com.stuartsierra.component :as component]
            [figwheel-sidecar.repl-api :refer [cljs-repl]]))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system (fn [s] (when s (component/stop s)))))

(defn reload [& {:keys [refresh-all?]}]
  (stop)
  (repl/set-refresh-dirs "src" "dev")
  (if refresh-all?
    (repl/refresh-all :after 'user/start)
    (repl/refresh :after 'user/start)))
