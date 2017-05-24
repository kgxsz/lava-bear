(ns client.system
  (:require [client.ui :as ui]
            [client.navigation :as n]
            [client.mutations :as m]
            [com.stuartsierra.component :as c]
            [om.next :as om]
            [untangled.client.core :as uc]
            [untangled.client.data-fetch :as ud]
            [untangled.client.logging :as log]))

(defrecord Config []
  c/Lifecycle
  (start [component]
    (log/info "starting config")
    (assoc component
           :client-routes ["/" [["" :home]
                                ["auth" :auth]
                                [true :unknown]]]))

  (stop [component]
    component))

(defrecord Browser [config]
  c/Lifecycle
  (start [component]
    (log/info "starting browser")
    (assoc component
           :navigation (atom {})))

  (stop [component]
    component))

(defrecord Renderer [config browser]
  c/Lifecycle
  (start [component]
    (let [shared {:browser browser
                  :config config}
          untangled-client (atom (uc/new-untangled-client
                                   :started-callback (fn [{:keys [reconciler]}]
                                                       (n/start-navigation reconciler (:navigation browser) (:client-routes config))
                                                       (ud/load-data reconciler [:current-user]))
                                   :shared shared))]
      (log/info "starting renderer")
      (swap! untangled-client uc/mount ui/App "app")
      (assoc component :untangled-client untangled-client)))

  (stop [component]
    component))

(defn make-system []
  (-> (c/system-map
       ;; TODO simplify here to look like server
       :config (map->Config {})
       :browser (map->Browser {})
       :renderer (map->Renderer {}))
      (c/system-using
       {:browser [:config]
        :renderer [:config :browser]})))

(defonce system (c/start (make-system)))
