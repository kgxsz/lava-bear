(ns server.main
  (:require [server.system :refer [make-system]]
            [clojure.tools.namespace.repl :as repl]
            [com.stuartsierra.component :as component]))

(defonce system (make-system))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system (fn [s] (when s (component/stop s)))))

(defn reload [& {:keys [refresh-all?]}]
  (stop)
  (repl/set-refresh-dirs "src" "dev")
  (if refresh-all?
    (repl/refresh-all :after 'server.main/start)
    (repl/refresh :after 'server.main/start)))
