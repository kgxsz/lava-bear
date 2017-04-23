(ns client.navigation
  (:require [bidi.bidi :as bidi]
            [cemerick.url :as url]
            [om.next :as om]
            [pushy.core :as pushy]
            [clojure.string :as s]
            [untangled.client.logging :as log]))

(defn navigate [{:keys [config browser]} {:keys [handler query route-params url replace?]}]
  (let [{:keys [client-routes]} config
        {:keys [!navigation]} browser]
    (if url
      (pushy/set-token! @!navigation url)
      (let [{:keys [client-routes]} config
            {:keys [!navigation]} browser
            query-string (when-not (s/blank? (url/map->query query)) (str "?" (url/map->query query)))
            route-params (-> route-params vec flatten)
            path (apply bidi/path-for client-routes handler route-params)]
        ((if replace? pushy/replace-token! pushy/set-token!) @!navigation (str path query-string))))))

(defn start-navigation [reconciler !navigation client-routes]
  (reset! !navigation (pushy/pushy
                       (fn [location]
                         ;; TODO - decompose the path here please
                         (js/console.warn "pushy detected a change")
                         (om/transact! reconciler `[(app/navigate ~location) :handler]))
                       (partial bidi/match-route client-routes)))
  (pushy/start! @!navigation))
