(ns client.system
  (:require [client.ui :as ui]
            [client.navigation :as n]
            [client.mutations :as m]
            [com.stuartsierra.component :as c]
            [om.next :as om]
            [untangled.client.core :as u]
            [untangled.client.data-fetch :as d]
            [untangled.client.logging :as log]))

(defrecord Config []
  c/Lifecycle
  (start [component]
    (log/info "starting config")
    (assoc component
           ;; TODO - should this come from somewhere common?
           :client-routes ["/" [["" :home]
                                [[:thing-id "/thing"] :thing]
                                ["facebook-sign-in" :facebook-sign-in]
                                [true :unknown]]]))

  (stop [component]
    component))

(defrecord Browser [config]
  c/Lifecycle
  (start [component]
    (log/info "starting browser")
    (assoc component
           :!navigation (atom {})))

  (stop [component]
    component))

;; TODO - should this actually be the untangled-client with a :value key?
(defrecord Renderer [config browser]
  c/Lifecycle
  (start [component]
    (let [shared {:browser browser
                  :config config}
          !untangled-client (atom (u/new-untangled-client
                                   :started-callback (fn [{:keys [reconciler]}]
                                                       (n/start-navigation reconciler (:!navigation browser) (:client-routes config))
                                                       (d/load-data reconciler [{:loaded-items (om/get-query ui/Item)}]
                                                                    :post-mutation 'fetch/items-loaded))
                                   :shared shared))]
      (log/info "starting renderer")
      (swap! !untangled-client u/mount ui/App "app")
      (assoc component :!untangled-client !untangled-client)))

  (stop [component]
    component))

(defn make-system []
  (-> (c/system-map
       :config (map->Config {})
       :browser (map->Browser {})
       :renderer (map->Renderer {}))
      (c/system-using
       {:browser [:config]
        :renderer [:config :browser]})))

(defonce system (c/start (make-system)))
