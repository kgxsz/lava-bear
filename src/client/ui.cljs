(ns client.ui
  (:require [om.next :as om :refer-macros [defui]]
            [untangled.client.core :as uc]
            [om-tools.dom :as dom :include-macros true]))

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
    :items [(uc/initial-state Item {:label "A"})
            (uc/initial-state Item {:label "B"})]})

  static om/IQuery
  (query
   [this]
   [:title
    {:items (om/get-query Item)}])

  static om/Ident
  (ident
   [this {:keys [title]}]
   [:lists/by-title title])

  Object
  (render
   [this]
   (let [{:keys [title items]} (om/props this)]
     (dom/div
      (dom/h4 title)
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
