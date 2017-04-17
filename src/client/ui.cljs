(ns client.ui
  (:require [om.next :as om :refer-macros [defui]]
            [om-tools.dom :as dom :include-macros true]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as m]))

#_(defui ^:once Item
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

#_(def ui-item (om/factory Item {:keyfn :label}))


#_(defui ^:once MyList
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

#_(def ui-my-list (om/factory MyList))


(defui ^:once Main
  static uc/InitialAppState
  (initial-state
   [this params]
   {:id 1
    :type :main-tab
    :extra "extra"})

  static om/IQuery
  (query
   [this]
   [:id :type :extra])

  Object
  (render
   [this]
   (let [{:keys [extra]} (om/props this)]
     (dom/p "main:" extra))))

(def ui-main (om/factory Main {:keyfn :id}))


(defui ^:once Settings
  static uc/InitialAppState
  (initial-state
   [this params]
   {:id 1
    :type :settings-tab
    :args {:a 1}})

  static om/IQuery
  (query
   [this]
   [:id :type :args])

  Object
  (render
   [this]
   (let [{:keys [args]} (om/props this)]
     (dom/p "settings:" (pr-str args)))))

(def ui-settings (om/factory Settings {:keyfn :id}))


(defui Switcher
  static uc/InitialAppState
  (initial-state
   [this params]
   (uc/initial-state Main {}))

  static om/IQuery
  (query
   [this]
   {:main-tab (om/get-query Main)
    :settings-tab (om/get-query Settings)})

  static om/Ident
  (ident
   [this {:keys [id type]}]
   [type id])

  Object
  (render
   [this]
   (let [{:keys [type] :as props} (om/props this)]
     (case type
       :main-tab (ui-main props)
       :settings-tab (ui-settings props)
       (dom/p "no tab")))))

(def ui-switcher (om/factory Switcher))


(defui ^:once App
  static uc/InitialAppState
  (initial-state
   [this params]
   {:ui/react-key :app
    :tabs (uc/initial-state Switcher {})})

  static om/IQuery
  (query
   [this]
   [:ui/react-key
    {:tabs (om/get-query Switcher)}])

  Object
  (render
   [this]
   (let [{:keys [ui/react-key tabs]} (om/props this)]
     (dom/div
      {:key react-key}
      (dom/h4 "Header")
      (dom/button
       {:on-click #(om/transact! this '[(app/choose-tab {:tab :main-tab})])}
       "main")
      (dom/button
       {:on-click #(om/transact! this '[(app/choose-tab {:tab :settings-tab})])}
       "settings")
      (ui-switcher tabs)))))
