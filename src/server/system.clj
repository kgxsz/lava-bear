(ns server.system
  (:gen-class)
  (:require [server.api :as api]
            [bidi.ring :as ring]
            [clojure.edn :as edn]
            [com.stuartsierra.component :as c]
            [hiccup.page :as page]
            [org.httpkit.server :as http]
            [ring.middleware.defaults :as md]
            [ring.util.response :as ur]
            [taoensso.timbre :as log]
            [untangled.server.core :as uc]
            [untangled.server.impl.middleware :as usm]))

(defrecord Config []
  c/Lifecycle
  (start [this]
    (let [private-config (try
                           (-> (str (System/getProperty "user.home") "/.lein/lava-bear/private_config.edn")
                               (slurp)
                               (edn/read-string))
                           (catch java.io.FileNotFoundException e {}))]

      (log/info "creating config")

      (assoc this
             :server-routes ["/" [[true :root-page]]]
             :http-kit-opts {:port (or (edn/read-string (System/getenv "PORT"))
                                       (get-in private-config [:port])
                                       3000)}
             :middleware-opts {:params {:urlencoded true
                                        :nested true
                                        :keywordize true}
                               :security {:xss-protection {:enable? true
                                                           :mode :block}
                                          :frame-options :sameorigin
                                          :content-type-options :nosniff}
                               :session {:flash true
                                         :cookie-attrs {:http-only true
                                                        :max-age 3600}}
                               :static {:resources "public"}
                               :responses {:not-modified-responses true
                                           :absolute-redirects true
                                           :content-types true
                                           :default-charset "utf-8"}})))

  ;; Need to grab ENV vars for each and every value here, like {:http-kit {:opts {:port 3000}}} would be HTTP_KIT_OPTS_PORT=3000
  ;; ONly grab the ones that ask for it though

  (stop [this] this))

(defrecord Database []
  c/Lifecycle
  (start [this]
    (log/info "creating database")
    (assoc this
           :last-id (atom 2)
           :items (atom [{:id 1 :label "item from server"}
                        {:id 2 :label "another item"}])))

  (stop [this] this))

(defrecord Server []
  c/Lifecycle
  (start [{:keys [config untangled.server.core/api-handler] :as this}]
    (let [{:keys [http-kit-opts middleware-opts server-routes]} config

          root-page (fn [request]
                      (let [root-page (page/html5
                                       [:head
                                        [:title "Lava Bear"]
                                        [:meta {:name "viewport"
                                                :content "width = device-width, initial-scale = 1.0, user-scalable = no"}]
                                        (page/include-css "/css/compiled/app.css")]
                                       [:body
                                        [:div#app]
                                        (page/include-js "/js/compiled/app.js")])]
                        (-> root-page ur/response (ur/content-type "text/html"))))

          handler (-> (ring/make-handler server-routes {:root-page root-page})
                      ((partial (:middleware api-handler)))
                      (md/wrap-defaults middleware-opts)
                      (usm/wrap-transit-params)
                      (usm/wrap-transit-response))

          stop-server (http/run-server handler http-kit-opts)]

      (log/info "starting server on port" (:port http-kit-opts))
      (assoc this :stop-server stop-server)))

  (stop [this]
    (when-let [stop-server (:stop-server this)]
      (log/info "stopping server")
      (stop-server))
    (assoc this :stop-server nil)))

(defrecord ApiModule []
  uc/Module
  (system-key [this] :api)
  (components [this] {})
  uc/APIHandler
  (api-read [this] api/api-read)
  (api-mutate [this] api/api-mutate))

(defonce system
  (uc/untangled-system
   {:components {:config (->Config)
                 :database (->Database)
                 :server (c/using (->Server) [:config ::uc/api-handler])}
    :modules [(c/using (->ApiModule) [:config :database])]}))

(defn -main [& args]
  (alter-var-root #'system c/start))
