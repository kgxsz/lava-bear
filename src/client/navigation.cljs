(ns client.navigation
  (:require [bidi.bidi :as bidi]
            [cemerick.url :as url]
            [medley.core :as m]
            [om.next :as om]
            [pushy.core :as pushy]
            [clojure.string :as s]
            [untangled.client.logging :as log]))

(defn navigate [{:keys [config browser]} {:keys [handler query-params route-params url replace?]}]
  (let [{:keys [client-routes]} config
        {:keys [!navigation]} browser
        query-string (when-not (s/blank? (url/map->query query-params)) (str "?" (url/map->query query-params)))
        route-params (-> route-params vec flatten)
        path (when-not url (apply bidi/path-for client-routes handler route-params))]
    ((if replace? pushy/replace-token! pushy/set-token!) @!navigation (or url (str path query-string)))))

(defn start-navigation [reconciler !navigation client-routes]
  (reset! !navigation (pushy/pushy
                       (fn [{:keys [handler route-params] :as location}]
                         (let [page (-> handler
                                        (name)
                                        (str "-page")
                                        (keyword))
                               query-params (->> (s/split (pushy/get-token @!navigation) "?")
                                                 (second)
                                                 (url/query->map)
                                                 (m/map-keys keyword))]
                           (om/transact! reconciler `[(app/navigate {:page ~page
                                                                     :handler ~handler
                                                                     :route-params ~route-params
                                                                     :query-params ~query-params})
                                                      :page])))
                       (partial bidi/match-route client-routes)))
  (pushy/start! @!navigation))
