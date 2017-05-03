;; TODO - shorten aliases all over please
(ns server.system
  (:gen-class)
  (:require [bidi.ring :as br]
            [com.stuartsierra.component :as component]
            [hiccup.page :refer [html5 include-js include-css]]

            ;; TODO - put in the right place
            [untangled.server.core :as usc]
            [untangled.server.impl.middleware :as mid]
            [om.next.server :as oms]

            [om.next :as om]
            [om.next.impl.parser :as parser]
            [org.httpkit.server :as http]
            [ring.middleware.defaults :as rmd]
            [ring.util.response :refer [response content-type]]
            [taoensso.timbre :as log]))

;; TODO - do something intelligent with this
(def items (atom [{:id 1 :label "item from server"}]))

(defmulti api-mutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod api-mutate :default [e k p]
  (log/error "unrecognised mutatuion " k))

(defmethod api-read :default [{:keys [ast query] :as env} dispatch-key params]
  (log/error "unrecognised query" (parser/ast->expr ast)))

(defn root-page-handler [request]
  (let [root-page (html5
                   [:head
                    [:title "Lava Bear"]
                    [:meta {:name "viewport" :content "width = device-width, initial-scale = 1.0, user-scalable = no"}]
                    (include-css "/css/compiled/app.css")]
                   [:body
                    [:div#app]
                    (include-js "/js/compiled/app.js")])]
    (-> root-page response (content-type "text/html"))))

(defrecord Config []
  component/Lifecycle
  (start [component]
    (log/info "starting config")
    (assoc component
           :value {:server-routes ["/" [[true :root-page-handler]]]
                   :port (Integer. (or (System/getenv "PORT") "3000"))
                   :middleware-opts {:params {:urlencoded true
                                              :nested true
                                              :keywordize true}
                                     :security {:xss-protection {:enable? true, :mode :block}
                                                :frame-options :sameorigin
                                                :content-type-options :nosniff}
                                     :session {:flash true
                                               :cookie-attrs {:http-only true
                                                              :max-age 3600}}
                                     :static {:resources "public"}
                                     :responses {:not-modified-responses true
                                                 :absolute-redirects true
                                                 :content-types true
                                                 :default-charset "utf-8"}}}))

  (stop [component] component))

(defn MIDDLEWARE [handler component]
  ((get component :middleware) handler))

;; TODO - apparently you can grab things like :config straight out of the component
(defrecord YourHandler [config api-handler]
  component/Lifecycle
  (start [component]
    (let [middleware-opts (get-in config [:value :middleware-opts])
          server-routes (get-in config [:value :server-routes])]

      (log/info "created handler")
      (assoc component :middleware (-> (br/make-handler server-routes {:root-page-handler root-page-handler})

                                       ;; TODO - improve this shit
                                       (MIDDLEWARE api-handler)

                                       (rmd/wrap-defaults middleware-opts)

                                       ;; TODO - wrap the encoding stuff here
                                       (mid/wrap-transit-params)
                                       (mid/wrap-transit-response)

                                       ))))

  (stop [component]
    (assoc component :middleware nil)))

(defrecord YourApiModule []
  usc/Module
  (system-key [this] ::YourApiModule)
  (components [this] {})
  usc/APIHandler
  (api-read [this] api-read)
  (api-mutate [this] api-mutate))

(defn make-your-api-module []
    (component/using (->YourApiModule) []))

#_(defrecord HttpServer [config]
  component/Lifecycle
  (start [component]
    (let [port (Integer. (get-in config [:http-server :port]))
          middleware-opts (get-in config [:http-server :middleware-opts])
          server-routes (get-in config [:server-routes])
          handler (-> (br/make-handler server-routes {:root-page root-page-handler})
                      (rmd/wrap-defaults middleware-opts))
          stop-http-server (http/run-server handler {:port port})]

      (log/info "starting http server on port" port)
      (assoc component :stop-http-server stop-http-server)))

  (stop [component]
    (when-let [stop-http-server (:stop-http-server component)]
      (log/info "stopping http server")
      (stop-http-server))
    (assoc component :stop-http-server nil)))

#_(defn make-system []
  (-> (component/system-map
       :config (map->Config {})
       :http-server (map->HttpServer {}))
      (component/system-using
       {:http-server [:config]})))

;; TODO - figure out how to get this guy where it needs to be
(defonce system
  (usc/untangled-system
   {:api-handler-key :api-handler ;; TODO - probs don't need this
    :components {:config (map->Config {})
                 :server (usc/make-web-server) ;;TODO - I can use my own server goddamnit
                 :handler (component/using (map->YourHandler {}) [:config :api-handler])}
    :modules [(->YourApiModule)]}))

#_(defonce system (make-system))

(defn -main [& args]
  (alter-var-root #'system component/start))
