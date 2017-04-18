(ns client.ui
  (:require [om.next :as om :refer-macros [defui]]
            [om-tools.dom :as dom :include-macros true]
            [pushy.core :as pushy]
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


#_(defui ^:once Main
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

#_(def ui-main (om/factory Main {:keyfn :id}))


#_(defui ^:once Settings
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

#_(def ui-settings (om/factory Settings {:keyfn :id}))


#_(defui Switcher
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

#_(def ui-switcher (om/factory Switcher))


(defui ^:once HomePage
  static uc/InitialAppState
  (initial-state
   [this params]
   {:id 1
    :type :home-page
    :extra "extra"})

  static om/IQuery
  (query
   [this]
   [:id :type :extra])

  Object
  (render
   [this]
   (let [{:keys [extra]} (om/props this)]
     (dom/p "home page: " extra))))

(def ui-home-page (om/factory HomePage))


(defui ^:once UnknownPage
  static uc/InitialAppState
  (initial-state
   [this params]
   {:id 1
    :type :unknown-page
    :boo "boo"})

  static om/IQuery
  (query
   [this]
   [:id :type :boo])

  Object
  (render
   [this]
   (let [{:keys [boo]} (om/props this)]
     (dom/p "unknown page:" boo))))

(def ui-unknown-page (om/factory UnknownPage))


(defui PageRouter
  static uc/InitialAppState
  (initial-state
   [this params]
   (uc/initial-state HomePage {}))

  static om/IQuery
  (query
   [this]
   {:home-page (om/get-query HomePage)
    :unknown-page (om/get-query UnknownPage)})

  static om/Ident
  (ident
   [this {:keys [id type]}]
   [type id])

  Object
  (render
   [this]
   (let [{:keys [type] :as props} (om/props this)]
     (case type
       :home-page (ui-home-page props)
       :unknown-page (ui-unknown-page props)
       (dom/p "no page!!!")))))

(def ui-page-router (om/factory PageRouter))


(defui ^:once App
  static uc/InitialAppState
  (initial-state
   [this params]
   {:ui/react-key :app
    :pages (uc/initial-state PageRouter {})
    #_:tabs #_(uc/initial-state Switcher {})})

  static om/IQuery
  (query
   [this]
   [:ui/react-key
    {:pages (om/get-query PageRouter)}
    #_{:tabs (om/get-query Switcher)}])

  Object
  (render
   [this]
   (let [{:keys [ui/react-key pages tabs]} (om/props this)
         {:keys [!history]} (om/shared this)]
     (dom/div
      {:key react-key}
      (dom/h4 "Header")
      (dom/button
       {:on-click #(pushy/set-token! @!history "/")}
       "home")
      (dom/button
       {:on-click #(pushy/set-token! @!history "/d")}
       "unknown")
      #_(dom/button
       {:on-click #(om/transact! this '[(app/choose-tab {:tab :main-tab})])}
       "main")
      #_(dom/button
       {:on-click #(om/transact! this '[(app/choose-tab {:tab :settings-tab})])}
       "settings")
      #_(ui-switcher tabs)
      (ui-page-router pages)))))
