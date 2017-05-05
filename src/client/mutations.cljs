(ns client.mutations
  (:require [client.ui :as ui]
            [om.next :as om]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as um]))

(defmethod um/mutate 'app/add-item [{:keys [state ref]} k {:keys [id label]}]
  {:remote true
   :action (fn []
             (let [list-path (conj ref :items)
                   new-item (uc/initial-state ui/Item {:id id :label label})
                   item-ident (om/ident ui/Item new-item)]
               ;; place the item in the db table items
               (swap! state assoc-in item-ident new-item)
               ;; tack on the ident of the item in the list
               (uc/integrate-ident! state item-ident :append list-path)))})

(defmethod um/mutate 'fetch/items-loaded [{:keys [state]} _ _]
  {:action (fn []
             (let [idents (get @state :loaded-items)]
               (swap! state (fn [s]
                              (-> s
                                  (assoc-in [:lists/by-title "Some List" :items] idents)
                                  (dissoc :loaded-items))))))})

(defmethod um/mutate 'app/navigate [{:keys [state]} _ {:keys [page handler route-params query-params]}]
  {:action (fn []
             (swap! state assoc
                    :navigation {:handler handler
                                 :route-params route-params
                                 :query-params query-params}
                    :page [page '_]))})
