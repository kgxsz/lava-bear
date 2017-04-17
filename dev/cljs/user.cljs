(ns ^:figwheel-no-load cljs.user
  (:require [client.system :refer [make-system]]
            [client.ui :as ui]
            [com.stuartsierra.component :as component]
            [devtools.core :as devtools]
            [untangled.client.core :as uc]
            [untangled.client.impl.util :as util]
            [untangled.client.logging :as log]))

(enable-console-print!)
(log/set-level :debug)
(devtools/install!)

(defonce system (component/start (make-system)))

(defn reload []
  (let [!untangled-client (get-in system [:renderer :!untangled-client])]
    (swap! !untangled-client uc/mount ui/App "app")))

(defn state []
  (let [!untangled-client (get-in system [:renderer :!untangled-client])]
    (util/log-app-state !untangled-client)))
