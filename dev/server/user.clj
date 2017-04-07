(ns server.user
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all set-refresh-dirs]]
            [ring.middleware.defaults :as rmd]
            [bidi.ring :as br]
            [org.httpkit.server :as http]
            [hiccup.page :refer [html5 include-js include-css]]
            [ring.util.response :refer [response content-type]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]))


(defn root-page-handler [request]
  (let [root-page (html5
                   [:head
                    [:title "Lava Bear"]
                    [:meta {:name "viewport" :content "width = device-width, initial-scale = 1.0, user-scalable = no"}]
                    (include-css "/css/app.css")]
                   [:body
                    [:div#app]
                    (include-js "/js/compiled/app.js")])]
    (-> root-page response (content-type "text/html"))))

;; TODO - move this shit to src/server and make the system directly in here
(defrecord HttpServer []
  component/Lifecycle
  (start [component]
    (let [port 3000
          middleware-opts {:params {:urlencoded true
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
                                       :default-charset "utf-8"}}

          backend-routes ["/" [[true :root-page]]]

          handler (-> (br/make-handler backend-routes {:root-page root-page-handler})
                      (rmd/wrap-defaults middleware-opts))

          stop-http-server (http/run-server handler {:port port})]

      (log/info "starting http server on port" port)
      (assoc component :stop-http-server stop-http-server)))

  (stop [component]
    (when-let [stop-http-server (:stop-http-server component)]
      (log/info "stopping http server")
      (stop-http-server))
    (assoc component :stop-http-server nil)))


(def system (component/system-map
             :http-server (map->HttpServer {})))


(defn start! []
  (alter-var-root #'system component/start))


(defn stop! []
  (alter-var-root #'system (fn [s] (when s (component/stop s)))))


(defn reload! [& {:keys [refresh-all?]}]
  (stop!)
  (set-refresh-dirs "server")
  (if refresh-all?
    (refresh-all :after 'server.user/start!)
    (refresh :after 'server.user/start!)))
