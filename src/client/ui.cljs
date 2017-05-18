(ns client.ui
  (:require [client.navigation :as n]
            [om.next :as om :refer-macros [defui]]
            [om-tools.dom :as dom :include-macros true]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as um]))

(defui ^:once Item
  static uc/InitialAppState
  (initial-state [this {:keys [id label]}]
    {:id id :label label})

  static om/IQuery
  (query [this]
    [:id :label])

  static om/Ident
  (ident [this {:keys [id label]}]
    [:items/by-id id])

  Object
  (render [this]
    (let [{:keys [label]} (om/props this)]
      (dom/li label))))

(def ui-item (om/factory Item {:keyfn :id}))

(defui ^:once MyList
  static uc/InitialAppState
  (initial-state [this params]
    {:title "Some List" :ui/new-item-label "" :items []})

  static om/IQuery
  (query [this]
    [:title :ui/new-item-label {:items (om/get-query Item)}])

  static om/Ident
  (ident [this {:keys [title]}]
    [:lists/by-title title])

  Object
  (render [this]
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

(defui ^:once ThingPage
  static uc/InitialAppState
  (initial-state [this params]
    {:id '_ :page :thing-page :list (uc/initial-state MyList {})})

  static om/IQuery
  (query [this]
    [:id :page {:list (om/get-query MyList)} {[:navigation '_] [:route-params]}])

  Object
  (render [this]
    (let [{:keys [navigation list]} (om/props this)]
      (dom/div
        (dom/p "thing page: " (pr-str navigation))
        (ui-my-list list)))))

(def ui-thing-page (om/factory ThingPage))

(defui ^:once AuthPage
  static uc/InitialAppState
  (initial-state [this params]
   {:id '_ :page :auth-page})

  static om/IQuery
  (query [this]
   [:id :page [:ui/auth-status '_] {[:navigation '_] [:query-params]}])

  Object
  (componentDidMount [this]
    (let [{:keys [error state code]} (get-in (om/props this) [:navigation :query-params])
          id (uuid state)]
      (cond
        error (om/transact! this `[(app/update-auth-status {:auth-status :failure})])
        (and state code) (om/transact! this `[(app/update-auth-status {:auth-status :loading})
                                              (app/finalise-auth-attempt {:id ~id :code ~code})
                                              #_(untangled/load {:query [(:auth-attempt {:id ~id})]
                                                               :post-mutation app/something})]))
      (n/navigate this {:handler :home})))

  (render [this]
    (dom/div
      "auth page - loading")))

(def ui-auth-page (om/factory AuthPage))

(defui ^:once HomePage
  static uc/InitialAppState
  (initial-state [this params]
    {:id '_ :page :home-page})

  static om/IQuery
  (query [this]
    [:id :page [:ui/auth-status '_] [:auth-attempt '_]])

  Object
  (componentDidUpdate [this _ _]
    (when-let [{:keys [app-id id redirect-url scope]} (:auth-attempt (om/props this))]
      (n/navigate this {:url "https://www.facebook.com/v2.8/dialog/oauth"
                        :query-params {:app_id app-id
                                       :state id
                                       :scope scope
                                       :redirect_uri redirect-url}})))

  (render [this]
    (let [{:keys [ui/auth-status]} (om/props this)]
      (dom/div
        (dom/button
          {:on-click #(let [tempid (om/tempid)]
                        (om/transact! this `[(app/update-auth-status {:auth-status :loading})
                                             (app/initialise-auth-attempt {:id ~tempid})
                                             (untangled/load {:query [(:auth-attempt {:id ~tempid})]})]))}
          (case auth-status
            :loading "signing in"
            :success "sign in succeeded!"
            :failure "sign in failed!"
            "sign in"))))))

(def ui-home-page (om/factory HomePage))

(defui ^:once UnknownPage
  static uc/InitialAppState
  (initial-state [this params]
    {:id '_ :page :unknown-page})

  static om/IQuery
  (query [this]
    [:id :page])

  Object
  (render [this]
    (dom/div
      (dom/p
        "You're lost")
      (dom/button
        {:on-click #(n/navigate this {:handler :home})}
        "go home"))))

(def ui-unknown-page (om/factory UnknownPage))

(defui ^:once PageRouter
  static uc/InitialAppState
  (initial-state [this params]
    (uc/initial-state HomePage {}))

  static om/IQuery
  (query [this]
    {:home-page (om/get-query HomePage)
     :auth-page (om/get-query AuthPage)
     :thing-page (om/get-query ThingPage)
     :unknown-page (om/get-query UnknownPage)})

  static om/Ident
  (ident [this {:keys [page id]}]
    [page id])

  Object
  (render [this]
    (let [{:keys [page] :as props} (om/props this)]
      (case page
        :home-page (ui-home-page props)
        :auth-page (ui-auth-page props)
        :thing-page (ui-thing-page props)
        (ui-unknown-page props)))))

(def ui-page-router (om/factory PageRouter))

(defui ^:once App
  static uc/InitialAppState
  (initial-state [this params]
    {:ui/react-key :app
     :ui/auth-status :idle
     :page (uc/initial-state PageRouter {})})

  static om/IQuery
  (query [this]
    [:ui/react-key
     :route-params
     {:page (om/get-query PageRouter)}])

  Object
  (render [this]
    (let [{:keys [ui/react-key page]} (om/props this)]
      (dom/div
        {:key react-key}
        (ui-page-router page)))))
