(ns client.navigation
  (:require [bidi.bidi :as b]
            [cemerick.url :as url]
            [clojure.string :as s]
            [medley.core :as mc]
            [om.next :as om]
            [pushy.core :as p]
            [taoensso.timbre :as log]))

(defn navigate-externally [_ {:keys [url query-params]}]
  (let [query-string (when-not (s/blank? (url/map->query query-params)) (str "?" (url/map->query query-params)))]
    (set! js/window.location (str url query-string))))

(defn navigate-internally [component {:keys [handler query-params route-params replace?]}]
  (let [{{:keys [client-routes]} :config {:keys [navigation]} :browser} (om/shared component)
        query-string (when-not (s/blank? (url/map->query query-params)) (str "?" (url/map->query query-params)))
        route-params (-> route-params vec flatten)
        replace-or-set (if replace? p/replace-token! p/set-token!)
        path (str (apply b/path-for client-routes handler route-params) query-string)]
    (replace-or-set @navigation path)))

(defn start-navigation [reconciler navigation client-routes]
  (reset! navigation (p/pushy
                       (fn [{:keys [handler route-params] :as location}]
                         (let [page (-> handler
                                        (name)
                                        (str "-page")
                                        (keyword))
                               query-params (->> (s/split (p/get-token @navigation) "?")
                                                 (second)
                                                 (url/query->map)
                                                 (mc/map-keys keyword))]
                           (om/transact! reconciler `[(app/navigate-internally {:page ~page
                                                                                :handler ~handler
                                                                                :route-params ~route-params
                                                                                :query-params ~query-params})
                                                      :navigation
                                                      :page])))
                       (partial b/match-route client-routes)))
  (p/start! @navigation))
