(ns ^:figwheel-no-load cljs.user
  (:require [client.system :refer [system]]
            [client.ui :as ui]
            [com.stuartsierra.component :as component]
            [devtools.core :as dc]
            [untangled.client.core :as uc]
            [untangled.client.impl.util :as uu]
            [untangled.client.logging :as log]))

(enable-console-print!)
(log/set-level :debug)
(dc/install!)

(defn reload []
  (let [untangled-client (get-in system [:renderer :untangled-client])]
    (swap! untangled-client uc/mount ui/App "app")))

(defn state []
  (let [untangled-client (get-in system [:renderer :untangled-client])]
    (uu/log-app-state untangled-client)))
