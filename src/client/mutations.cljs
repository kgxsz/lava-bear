(ns client.mutations
  (:require [client.ui :as ui]
            [om.next :as om]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as m]))

(defmethod m/mutate 'app/add-item [{:keys [state ref]} k {:keys [label]}]
  {:action (fn []
             (let [list-path (conj ref :items)
                   new-item (uc/initial-state ui/Item {:id (rand-int 100) :label label})
                   item-ident (om/ident ui/Item new-item)]
               ;; place the item in the db table items
               (swap! state assoc-in item-ident new-item)
               ;; tack on the ident of the item in the list
               (uc/integrate-ident! state item-ident :append list-path)))})

(defmethod m/mutate 'fetch/items-loaded [{:keys [state]} _ _]
  {:action (fn []
             (js/console.warn "items loaded yo!")
             (js/console.warn (pr-str @state)))})

(defmethod m/mutate 'app/navigate [{:keys [state]} _ {:keys [page handler route-params query-params]}]
  {:action (fn []
             (swap! state assoc
                    :navigation {:handler handler
                                 :route-params route-params
                                 :query-params query-params}
                    :page [page '_]))})
