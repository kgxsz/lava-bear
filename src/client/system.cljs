(ns client.system
  (:require [client.ui :as ui]
            [client.mutations :as m]
            [bidi.bidi :as bidi]
            [com.stuartsierra.component :as component]
            [pushy.core :as pushy]
            [untangled.client.core :as uc]
            [untangled.client.logging :as log]
            [om.next :as om]))

(defrecord Config []
  component/Lifecycle
  (start [component]
    (log/info "starting config")
    (assoc component
           :client-routes ["/" [["" :home-page]
                                ["facebook-sign-in" :facebook-sign-in-page]
                                [true :unknown-page]]]))

  (stop [component]
    component))

(defrecord Browser [config]
  component/Lifecycle
  (start [component]
    (log/info "starting browser")
    (assoc component
           :!history (atom {})))

  (stop [component]
    component))

(defrecord Renderer [config browser]
  component/Lifecycle
  (start [component]
    (let [!untangled-client (atom (uc/new-untangled-client
                                   :started-callback (fn [{:keys [reconciler]}]
                                                       (let [client-routes (get-in config [:client-routes])
                                                             !history (get-in browser [:!history])
                                                             update-location (fn [location]
                                                                               (om/transact! reconciler `[(nav/update-location ~location) :pages]))]
                                                         (reset! !history (pushy/pushy update-location (partial bidi/match-route client-routes)))
                                                         (pushy/start! @!history)))
                                   :shared {:!history (get-in browser [:!history])}))]
      (log/info "starting renderer")
      (swap! !untangled-client uc/mount ui/App "app")
      (assoc component :!untangled-client !untangled-client)))

  (stop [component]
    component))

(defn make-system []
  (-> (component/system-map
       :config (map->Config {})
       :browser (map->Browser {})
       :renderer (map->Renderer {}))
      (component/system-using
       {:browser [:config]
        :renderer [:config :browser]})))

(defonce system (component/start (make-system)))
