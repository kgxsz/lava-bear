(ns client.ui
  (:require [client.navigation :as n]
            [bidi.bidi :as bidi]
            [om.next :as om :refer-macros [defui]]
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


(defui ^:once HomePage
  static uc/InitialAppState
  (initial-state
   [this params]
   {:id '_
    :handler :home
    :extra "extra"})

  static om/IQuery
  (query
   [this]
   [:id :handler :extra])

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
   {:id '_
    :handler :unknown
    :boo "boo"})

  static om/IQuery
  (query
   [this]
   [:id :handler :boo])

  Object
  (render
   [this]
   (let [{:keys [boo]} (om/props this)]
     (dom/p "unknown page: " boo))))

(def ui-unknown-page (om/factory UnknownPage))


(defui PageRouter
  static uc/InitialAppState
  (initial-state
   [this params]
   (uc/initial-state HomePage {}))

  static om/IQuery
  (query
   [this]
   {:home (om/get-query HomePage)
    :unknown (om/get-query UnknownPage)})

  static om/Ident
  (ident
   [this {:keys [id handler]}]
   [handler id])

  Object
  (render
   [this]
   (let [{:keys [handler] :as props} (om/props this)]
     (case handler
       :home (ui-home-page props)
       :unknown (ui-unknown-page props)
       (ui-unknown-page props)))))

(def ui-page-router (om/factory PageRouter))


(defui ^:once App
  static uc/InitialAppState
  (initial-state
   [this params]
   {:ui/react-key :app
    :handler (uc/initial-state PageRouter {})})

  static om/IQuery
  (query
   [this]
   [:ui/react-key
    {:handler (om/get-query PageRouter)}])

  Object
  (render
   [this]
   (let [{:keys [ui/react-key handler]} (om/props this)]
     (dom/div
      {:key react-key}
      (dom/h4 "Header")
      ;; TODO - these should be change-location functions that take this and know how to pull out shared
      (dom/button
       {:on-click #(n/navigate this "/")}
       "home")
      (dom/button
       {:on-click #(n/navigate this "/d")}
       "unknown")
      (ui-page-router handler)))))
