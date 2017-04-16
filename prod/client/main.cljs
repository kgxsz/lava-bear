(ns client.main
  (:require [client.system :refer [make-system]]
            [com.stuartsierra.component :as component]))

(defonce system (component/start (make-system)))
