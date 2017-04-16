(ns server.main
  (:gen-class)
  (:require [server.system :refer [make-system]]
            [com.stuartsierra.component :as component]))

(defonce system (make-system))

(defn start []
  (alter-var-root #'system component/start))

(defn -main [& args]
  (start))
