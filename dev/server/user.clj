(ns server.user
  (:require [server.system :as system]
            [clojure.tools.namespace.repl :as repl]
            [com.stuartsierra.component :as component]))

(def system (system/make-system))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system (fn [s] (when s (component/stop s)))))

(defn reload [& {:keys [refresh-all?]}]
  (stop)
  (repl/set-refresh-dirs "server")
  (if refresh-all?
    (repl/refresh-all :after 'server.user/start)
    (repl/refresh :after 'server.user/start)))
