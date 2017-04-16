(ns client.system
  (:require [client.ui :as ui]
            [com.stuartsierra.component :as component]
            [untangled.client.core :as uc]
            [untangled.client.logging :as log]))

(defn mount-app [{:keys [!untangled-client]}]
  (swap! !untangled-client uc/mount ui/App "app"))

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
      (mount-app {:!untangled-client !untangled-client})
      (assoc component :!untangled-client !untangled-client)))

  (stop [component]
    component))

(defn make-system []
  (-> (component/system-map
       :browser (map->Browser {})
       :renderer (map->Renderer {}))
      (component/system-using
       {:renderer [:browser]})))
