(ns client.system
  (:require [client.ui :as ui]
            [client.navigation :as n]
            [client.mutations :as m]
            [com.stuartsierra.component :as component]
            [untangled.client.core :as uc]
            [untangled.client.logging :as log]))

(defrecord Config []
  component/Lifecycle
  (start [component]
    (log/info "starting config")
    (assoc component
           :client-routes ["/" [["" :home]
                                [[:thing-id "/thing"] :thing]
                                ["facebook-sign-in" :facebook-sign-in]
                                [true :unknown]]]))

  ;; TODO - figure out how to make route-params work

  (stop [component]
    component))

(defrecord Browser [config]
  component/Lifecycle
  (start [component]
    (log/info "starting browser")
    (assoc component
           :!navigation (atom {})))

  (stop [component]
    component))

(defrecord Renderer [config browser]
  component/Lifecycle
  (start [component]
    (let [shared {:browser browser
                  :config config}
          !untangled-client (atom (uc/new-untangled-client
                                   :started-callback (fn [{:keys [reconciler]}]
                                                       (n/start-navigation reconciler (:!navigation browser) (:client-routes config)))
                                   :shared shared))]
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
