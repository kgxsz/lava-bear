(ns client.ui
  (:require [client.navigation :as n]
            [om.next :as om :refer-macros [defui]]
            [om-tools.dom :as dom :include-macros true]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as um]))

(defui ^:once Item
  static uc/InitialAppState
  (initial-state
   [this {:keys [id label]}]
   {:id id
    :label label})

  static om/IQuery
  (query
   [this]
   [:id
    :label])

  static om/Ident
  (ident
   [this {:keys [id label]}]
   [:items/by-id id])

  Object
  (render
   [this]
   (let [{:keys [label]} (om/props this)]
     (dom/li label))))

(def ui-item (om/factory Item {:keyfn :id}))


(defui ^:once MyList
  static uc/InitialAppState
  (initial-state
   [this params]
   {:title "Some List"
    :ui/new-item-label ""
    :items []})

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
                     (um/set-string! this :ui/new-item-label :event e))})
      (dom/button
       {:on-click (fn []
                    (um/set-string! this :ui/new-item-label :value "")
                    (om/transact! this `[(app/add-item {:id ~(om/tempid) :label ~new-item-label})
                                         (untangled/load {:query [{:loaded-items ~(om/get-query Item)}]
                                                          :post-mutation fetch/items-loaded})]))}
       "+")
      (dom/ul (map ui-item items))))))

(def ui-my-list (om/factory MyList))


(defui ^:once HomePage
  static uc/InitialAppState
  (initial-state
   [this params]
   {:id '_
    :page :home-page})

  static om/IQuery
  (query
   [this]
   [:id
    :page
    [:navigation '_]])

  Object
  (render
   [this]
   (let [{:keys [navigation]} (om/props this)]
     (dom/div
      {:style {:margin-top "8rem"
               :font-family "arial"
               :color "#555"
               :font-size "1.8rem"
               :text-align "center"}}
      "Under Construction")
     #_(dom/p "home page: " (pr-str navigation)))))


(def ui-home-page (om/factory HomePage))


(defui ^:once ThingPage
  static uc/InitialAppState
  (initial-state
   [this params]
   {:id '_
    :page :thing-page
    :list (uc/initial-state MyList {})})

  static om/IQuery
  (query
   [this]
   [:id
    :page
    {:list (om/get-query MyList)}
    {[:navigation '_] [:route-params]}])

  Object
  (render
   [this]
   (let [{:keys [navigation list]} (om/props this)]
     (dom/div
      (dom/p "thing page: " (pr-str navigation))
      (ui-my-list list)))))

(def ui-thing-page (om/factory ThingPage))


(defui ^:once UnknownPage
  static uc/InitialAppState
  (initial-state
   [this params]
   {:id '_
    :page :unknown-page})

  static om/IQuery
  (query
   [this]
   [:id
    :page])

  Object
  (render
   [this]
   (dom/p "unknown page :(")))

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
    :thing-page (om/get-query ThingPage)
    :unknown-page (om/get-query UnknownPage)})

  static om/Ident
  (ident
   [this {:keys [page id]}]
   [page id])

  Object
  (render
   [this]
   (let [{:keys [page] :as props} (om/props this)]
     (case page
       :home-page (ui-home-page props)
       :thing-page (ui-thing-page props)
       (ui-unknown-page props)))))

(def ui-page-router (om/factory PageRouter))


(defui ^:once App
  static uc/InitialAppState
  (initial-state
   [this params]
   {:ui/react-key :app
    :page (uc/initial-state PageRouter {})})

  static om/IQuery
  (query
   [this]
   [:ui/react-key
    :ui/loading-data
    :route-params
    {:page (om/get-query PageRouter)}])

  Object
  (render
   [this]
   (let [{:keys [ui/react-key ui/loading-data page]} (om/props this)]
     (dom/div
      {:key react-key}
      #_(dom/h4 "Header" (when loading-data " is loading"))
      #_(dom/button
       {:on-click #(n/navigate (om/shared this) {:handler :home :query-params {:a 1 :b "hello"}})}
       "home")
      #_(dom/button
       {:on-click #(n/navigate (om/shared this) {:handler :thing :route-params {:thing-id 123}})}
       "thing")
      #_(dom/button
       {:on-click #(n/navigate (om/shared this) {:handler :thing :route-params {:thing-id 69}})}
       "be cool")
      #_(dom/button
       {:on-click #(n/navigate (om/shared this) {:url "/wat"})}
       "unknown")
      (ui-page-router page)))))
