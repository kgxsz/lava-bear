(ns client.ui
  (:require [om.next :as om :refer-macros [defui]]
            [om-tools.dom :as dom :include-macros true]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as m]))

(defui ^:once Item
  static uc/InitialAppState
  (initial-state
   [this {:keys [label]}]
   {:label label})

  static om/IQuery
  (query
   [this]
   [:label])

  static om/Ident
  (ident
   [this {:keys [label]}]
   [:items/by-label label])

  Object
  (render
   [this]
   (let [{:keys [label]} (om/props this)]
     (dom/li label))))

(def ui-item (om/factory Item {:keyfn :label}))


(defui ^:once MyList
  static uc/InitialAppState
  (initial-state
   [this params]
   {:title "Initial List"
    :ui/new-item-label ""
    :items [(uc/initial-state Item {:label "A"})
            (uc/initial-state Item {:label "B"})]})

  static om/IQuery
  (query
   [this]
   [:title
    :ui/new-item-label
    {:items (om/get-query Item)}])

  static om/Ident
  (ident
   [this {:keys [title]}]
   [:lists/by-title title])

  Object
  (render
   [this]
   (let [{:keys [title ui/new-item-label items] :or {ui/new-item-label ""}} (om/props this)]
     (dom/div
      (dom/h4
       title)
      (dom/input
       {:value new-item-label
        :on-change (fn [e]
                     (m/set-string! this :ui/new-item-label :event e))})
      (dom/button
       {:on-click (fn []
                    (om/transact! this `[(app/add-item {:label ~new-item-label})]))}
       "+")
      (dom/ul (map ui-item items))))))

(def ui-my-list (om/factory MyList))


(defui ^:once App
  static uc/InitialAppState
  (initial-state
   [this params]
   {:list (uc/initial-state MyList {})})

  static om/IQuery
  (query
   [this]
   [:ui/react-key
    {:list (om/get-query MyList)}])

  Object
  (render
   [this]
   (let [{:keys [ui/react-key list]} (om/props this)]
     (dom/div
      {:key react-key}
      (dom/h4 "Some Lists")
      (ui-my-list list)))))
