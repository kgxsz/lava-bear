(ns server.system
  (:gen-class)
  (:require [server.api :as api]
            [shared.util :as util]
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
             :http-kit-opts {:port (or (edn/read-string (System/getenv "PORT")) 3000)}
             :middleware-opts {:params {:urlencoded true
                                        :nested true
                                        :keywordize true}
                               :security {:xss-protection {:enable? true :mode :block}
                                          :frame-options :sameorigin
                                          :content-type-options :nosniff}
                               :cookies true
                               :static {:resources "public"}
                               :responses {:not-modified-responses true
                                           :absolute-redirects true
                                           :content-types true
                                           :default-charset "utf-8"}}
             :auth {:client-id (or (System/getenv "AUTH_CLIENT_ID") "424679067898674")
                    :client-secret (or (System/getenv "AUTH_CLIENT_SECRET") (:auth-client-secret private-config))
                    :redirect-url (or (System/getenv "AUTH_REDIRECT_URL") "http://localhost:3000/auth")
                    :scope "email"})))

  (stop [this]
    this))

(defrecord State []
  c/Lifecycle
  (start [this]
    (log/info "creating state")
    (assoc this
           :sessions (atom {})
           :database (atom {:auth-attempts/by-id {}
                            :users/by-id {}
                            :last-id 2
                            :items [{:id 1 :label "item from server"}
                                    {:id 2 :label "another item"}]})))
  (stop [this]
    this))

(defn wrap-session [handler {:keys [state config]}]
  (fn [request]
    (let [session-key (or (some-> request (get-in [:cookies "session-key" :value]) java.util.UUID/fromString)
                          (java.util.UUID/randomUUID))
          process-request (fn [request]
                            (assoc request :session-key session-key))
          process-response (fn [response]
                             (if (get @(:sessions state) session-key)
                               (update response :cookies assoc "session-key" {:value session-key :http-only true :max-age 60})
                               response))]
      (-> request
          (process-request)
          (handler)
          (process-response)))))

(defn wrap-api [handler {:keys [untangled.server.core/api-handler]}]
  ((:middleware api-handler) handler))

(defrecord Server []
  c/Lifecycle
  (start [{:keys [config] :as this}]
    (let [{:keys [http-kit-opts middleware-opts server-routes]} config
          root-page (fn [request]
                      (let [root-page (page/html5
                                       [:head
                                        [:title "keigo.io"]
                                        [:meta {:name "viewport"
                                                :content "width = device-width, initial-scale = 1.0, user-scalable = no"}]
                                        (page/include-css "/css/compiled/app.css")]
                                       [:body
                                        [:div#js-app
                                         [:div.c-mascot
                                          (util/embed-svg "mascot-initial.svg")]
                                         [:div.c-loader
                                          "loading"]]
                                        (page/include-js "/js/compiled/app.js")])]
                        (-> root-page ur/response (ur/content-type "text/html"))))
          handler (-> (ring/make-handler server-routes {:root-page root-page})
                      (wrap-api this)
                      (wrap-session this)
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
                 :state (->State)
                 :server (c/using (->Server) [:config :state ::uc/api-handler])}
    :modules [(c/using (->ApiModule) [:config :state])]}))

(defn -main [& args]
  (alter-var-root #'system c/start))
