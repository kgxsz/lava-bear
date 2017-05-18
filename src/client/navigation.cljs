(ns client.navigation
  (:require [bidi.bidi :as b]
            [cemerick.url :as url]
            [medley.core :as mc]
            [om.next :as om]
            [pushy.core :as p]
            [clojure.string :as s]
            [untangled.client.logging :as log]))

(defn navigate [component {:keys [handler query-params route-params url replace?]}]
  (let [{{:keys [client-routes]} :config {:keys [navigation]} :browser} (om/shared component)
        query-string (when-not (s/blank? (url/map->query query-params)) (str "?" (url/map->query query-params)))
        route-params (-> route-params vec flatten)
        path (when-not url (apply b/path-for client-routes handler route-params))]
    (if url
      (set! js/window.location (str url query-string))
      ((if replace? p/replace-token! p/set-token!) @navigation (str path query-string)))))

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
                           (om/transact! reconciler `[(app/navigate {:page ~page
                                                                     :handler ~handler
                                                                     :route-params ~route-params
                                                                     :query-params ~query-params})
                                                      :page])))
                       (partial b/match-route client-routes)))
  (p/start! @navigation))
