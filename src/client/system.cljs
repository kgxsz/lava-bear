(ns client.system
  (:require [client.ui :as ui]
            [client.mutations :as m]
            [com.stuartsierra.component :as component]
            [untangled.client.core :as uc]
            [untangled.client.logging :as log]))

(defrecord Browser []
  component/Lifecycle
  (start [component]
    (log/info "starting browser")
    component)

  (stop [component]
    component))

(defrecord Renderer []
  component/Lifecycle
  (start [component]
    (let [!untangled-client (atom (uc/new-untangled-client))]
      (log/info "starting renderer")
      (swap! !untangled-client uc/mount ui/App "app")
      (assoc component :!untangled-client !untangled-client)))

  (stop [component]
    component))

(defn make-system []
  (-> (component/system-map
       :browser (map->Browser {})
       :renderer (map->Renderer {}))
      (component/system-using
       {:renderer [:browser]})))

(defonce system (component/start (make-system)))
