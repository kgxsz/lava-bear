(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all set-refresh-dirs]]
            [ring.middleware.defaults :as rmd]
            [bidi.ring :as br]
            [org.httpkit.server :refer [run-server]]
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


(defrecord Server []
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

          stop-server (run-server handler {:port port})]

      (log/info "starting server on port" port)
      (assoc component :stop-server stop-server)))

  (stop [component]
    (when-let [stop-server (:stop-server component)]
      (log/info "stopping server")
      (stop-server))
    (assoc component :stop-server nil)))


(def system (component/system-map
             :server (map->Server {})))


(defn start! []
  (alter-var-root #'system component/start))


(defn stop! []
  (alter-var-root #'system (fn [s] (when s (component/stop s)))))


(defn reload! [& {:keys [refresh-all?]}]
  (stop!)
  (set-refresh-dirs "server")
  (if refresh-all?
    (refresh-all :after 'user/start!)
    (refresh :after 'user/start!)))
