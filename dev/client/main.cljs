(ns ^:figwheel-no-load client.main
  (:require [client.system :refer [make-system mount-app]]
            [com.stuartsierra.component :as component]
            [devtools.core :as devtools]
            [untangled.client.logging :as log]))

(enable-console-print!)

(log/set-level :debug)

(devtools/install!)

(defonce system (component/start (make-system)))

(defn reload []
  (mount-app (:renderer system)))
