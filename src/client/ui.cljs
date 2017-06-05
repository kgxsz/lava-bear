(ns client.ui
  (:require [client.navigation :as n]
            [om.next :as om :refer-macros [defui]]
            [om-tools.dom :as dom :include-macros true]
            [untangled.client.core :as uc]
            [untangled.client.data-fetch :as ud]
            [untangled.client.mutations :as um]))

(defui ^:once Loading
  Object
  (render [this]
   (dom/div "loading")))

(def ui-loading (om/factory Loading))

(defui ^:once AuthPage
  static uc/InitialAppState
  (initial-state [this params]
   {:id '_ :page :auth-page})

  static om/IQuery
  (query [this]
    [:id :page [:current-user '_] {[:navigation '_] [:query-params]}])

  Object
  (componentDidMount [this]
    (let [{:keys [navigation]} (om/props this)
          {:keys [state code]} (:query-params navigation)
          auth-attempt-id (uuid state)]
      (when (and state code)
        (om/transact! this `[(app/finalise-auth-attempt {:id ~auth-attempt-id :code ~code})
                             (untangled/load {:query [:current-user (:auth-attempt {:id ~auth-attempt-id})]})]))))

  (componentDidUpdate [this _ _]
    (n/navigate this {:handler :home}))

  (render [this]
    (let [{:keys [navigation]} (om/props this)
          {:keys [error state code]} (:query-params navigation)]
      (dom/div
       (if error
         "mmmm something didn't quite work"
         ;; TODO - this should look exactly like the loading page
         "auth page - loading")))))

(def ui-auth-page (om/factory AuthPage))

(defui ^:once HomePage
  static uc/InitialAppState
  (initial-state [this params]
    {:id '_ :page :home-page})

  static om/IQuery
  (query [this]
    [:id :page [:current-user '_] [:auth-attempt '_]])

  Object
  (componentDidUpdate [this _ _]
    (let [{:keys [auth-attempt]} (om/props this)
          {:keys [initialised-at failure-at success-at id client-id redirect-url scope]} auth-attempt]
      (when (and initialised-at (nil? failure-at) (nil? success-at))
        (n/navigate this {:url "https://www.facebook.com/v2.9/dialog/oauth"
                          :query-params {:client_id client-id
                                         :state id
                                         :scope scope
                                         :redirect_uri redirect-url}}))))

  (render [this]
    (let [{:keys [current-user auth-attempt]} (om/props this)
          {:keys [first-name]} current-user
          {:keys [failure-at]} auth-attempt]
      (dom/div
       (if (empty? current-user)
         (dom/div
          (dom/button
           ;; TODO disable if in progress
           {:on-click #(let [tempid (om/tempid)]
                         (om/transact! this `[(app/initialise-auth-attempt {:id ~tempid})
                                              (untangled/load {:query [(:auth-attempt {:id ~tempid})]})]))}
           (cond
             failure-at "sign in failed!"
             auth-attempt "signing-in"
             :else "sign in")))

         (dom/div
          "Hi " first-name))))))

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
    (uc/initial-state UnknownPage {}))

  static om/IQuery
  (query [this]
    {:home-page (om/get-query HomePage)
     :auth-page (om/get-query AuthPage)
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
        (ui-unknown-page props)))))

(def ui-page-router (om/factory PageRouter))

(defui ^:once App
  static uc/InitialAppState
  (initial-state [this params]
    {:ui/react-key :app
     :navigation nil
     :current-user nil
     :page-router (uc/initial-state PageRouter {})})

  static om/IQuery
  (query [this]
    [:ui/react-key
     :navigation
     :current-user
     {:page-router (om/get-query PageRouter)}])

  Object
  (render [this]
    (let [{:keys [ui/react-key navigation current-user page-router]} (om/props this)
          initialising? (or (nil? navigation)
                            (nil? current-user)
                            (:ui/fetch-state current-user))]
      (dom/div
       {:key react-key}
       (if initialising?
         (ui-loading)
         (ui-page-router page-router))))))
