(ns client.mutations
  (:require [client.ui :as ui]
            [om.next :as om]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as m]))

#_(defmethod m/mutate 'app/add-item [{:keys [state ref]} k {:keys [label]}]
  {:action (fn []
             (let [list-path (conj ref :items)
                   new-item (uc/initial-state ui/Item {:label label})
                   item-ident (om/ident ui/Item new-item)]
               ;; place the item in the db table items
               (swap! state assoc-in item-ident new-item)
               ;; tack on the ident of the item in the list
               (uc/integrate-ident! state item-ident :append list-path)))})

#_(defmethod m/mutate 'app/choose-tab [{:keys [state]} _ {:keys [tab]}]
  ;; secretary calls this transaction for routing
  {:action (fn []
             (swap! state assoc :tabs [tab 1]))})

(defmethod m/mutate 'app/navigate [{:keys [state]} _ {:keys [handler route-params query-params]}]
  {:action (fn []
             (swap! state assoc
                    :handler [handler '_]
                    :route-params route-params
                    :query-params query-params))})
