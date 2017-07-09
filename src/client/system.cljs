(ns client.system
  (:require [client.ui :as ui]
            [client.navigation :as n]
            [client.mutations :as m]
            [com.stuartsierra.component :as c]
            [om.next :as om]
            [taoensso.timbre :as log]
            [untangled.client.core :as uc]
            [untangled.client.data-fetch :as ud]))

(defrecord Config []
  c/Lifecycle
  (start [this]
    (log/info "starting config")
    (assoc this
           :client-routes ["/" [["" :home]
                                [true :unknown]]]))

  (stop [this]
    this))

(defrecord Browser []
  c/Lifecycle
  (start [{:keys [config] :as this}]
    (log/info "starting browser")
    (assoc this
           :navigation (atom {})))

  (stop [this]
    this))

(defrecord Renderer []
  c/Lifecycle
  (start [{:keys [config browser] :as this}]
    (let [shared {:browser browser :config config}
          untangled-client (atom (uc/new-untangled-client
                                   :started-callback (fn [{:keys [reconciler]}]
                                                       (n/start-navigation reconciler (:navigation browser) (:client-routes config)))
                                   :shared shared))]
      (log/info "starting renderer")
      (swap! untangled-client uc/mount ui/App "js-root")
      (assoc this :untangled-client untangled-client)))

  (stop [this]
    this))

(defn make-system []
  (c/system-map
   :config (->Config)
   :browser (c/using (->Browser) [:config])
   :renderer (c/using (->Renderer) [:config :browser])))

(defonce system (c/start (make-system)))
