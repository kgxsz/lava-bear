(ns client.navigation
  (:require [bidi.bidi :as bidi]
            [om.next :as om]
            [pushy.core :as pushy]
            [untangled.client.logging :as log]))

(defn navigate [component path]
  (let [{{:keys [!navigation]} :browser, {:keys [client-routes]} :config} (om/shared component)]
    (js/console.warn "navigating:" (pr-str path))
    ;; TODO - build up the path here please
    (pushy/set-token! @!navigation path)))

(defn start-navigation [reconciler !navigation client-routes]
  (reset! !navigation (pushy/pushy
                       (fn [location]
                         ;; TODO - decompose the path here please
                         (js/console.warn "pushy detected a change")
                         (om/transact! reconciler `[(app/navigate ~location) :page]))
                       (partial bidi/match-route client-routes)))
  (pushy/start! @!navigation))
