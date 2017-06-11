(ns client.ui
  (:require [client.navigation :as n]
            [shared.util :as util]
            [om.next :as om :refer-macros [defui]]
            [om-tools.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [untangled.client.core :as uc]
            [untangled.client.data-fetch :as ud]
            [untangled.client.mutations :as um]))

(defui ^:once Loading
  Object
  (render [this]
    (dom/div
     (dom/div
      {:class "c-mascot-container"}
      (html (util/embed-svg "mascot-initial.svg")))
     (dom/div
      {:class "c-loader"}
      "loading"))))

(def ui-loading (om/factory Loading))

(defui ^:once AuthPage
  static uc/InitialAppState
  (initial-state [this params]
   {:id '_ :page :auth-page})

  static om/IQuery
  (query [this]
    [:id :page [:current-user '_] [:auth-attempt '_] [:navigation '_]])

  Object
  (componentDidMount [this]
    (let [{:keys [current-user auth-attempt navigation]} (om/props this)
          {:keys [error state code]} (:query-params navigation)
          can-finalise-auth-attempt? (and state code (nil? error) (nil? auth-attempt))
          can-redirect-to-home? (and (:user-id current-user) (:success-at auth-attempt))]
      (cond
        can-finalise-auth-attempt? (let [auth-attempt-id (uuid state)]
                                     (om/transact! this `[(app/finalise-auth-attempt {:id ~auth-attempt-id :code ~code})
                                                          (untangled/load {:query [:current-user (:auth-attempt {:id ~auth-attempt-id})]})]))
        can-redirect-to-home? (n/navigate this {:handler :home :replace? true}))))

  (render [this]
    (let [{:keys [auth-attempt navigation]} (om/props this)
          {:keys [query-params]} navigation
          error? (or (empty? query-params)
                     ;; error during facebook redirect
                     (:error query-params)
                     ;; no auth-attempt could be matched from backend
                     (and (nil? (:ui/fetch-state auth-attempt)) (map? auth-attempt) (empty? auth-attempt))
                     ;; auth-attempt finalisation failed on backend
                     (:failure-at auth-attempt))]
      (dom/div
       (if error?
         "something isn't right"
         (ui-loading))))))

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
          {:keys [initialised-at failure-at success-at id client-id redirect-url scope]} auth-attempt
          can-redirect-to-facebook? (and initialised-at (nil? failure-at) (nil? success-at))]
      (when can-redirect-to-facebook?
        (n/navigate this {:url "https://www.facebook.com/v2.9/dialog/oauth"
                          :query-params {:client_id client-id
                                         :state id
                                         :scope scope
                                         :redirect_uri redirect-url}}))))

  (render [this]
    (let [{:keys [current-user auth-attempt]} (om/props this)
          {:keys [first-name]} current-user
          can-initialise-auth-attempt? (nil? auth-attempt)]
      (dom/div

       (dom/div
        {:class "c-mascot-container"}
        (dom/div
         {:class "c-mascot-container__sprites c-mascot-container__sprites--blink"}
         (html (util/embed-svg "mascot-sprites.svg"))))

       ;; TODO - bring this back in when ready
       #_(if (:user-id current-user)
         (dom/div
          "Hi " first-name)

         (dom/button
          {:disabled (not can-initialise-auth-attempt?)
           :on-click #(let [tempid (om/tempid)]
                        (when can-initialise-auth-attempt?
                          (om/transact! this `[(app/initialise-auth-attempt {:id ~tempid})
                                               (untangled/load {:query [(:auth-attempt {:id ~tempid})]})])))}
          (cond
            auth-attempt "signing in"
            :else "sign in")))))))

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
    (let [{:keys [ui/react-key navigation current-user page-router]} (om/props this)]
      (dom/div
       {:key react-key}
       (if (and (seq navigation) (map? current-user) (nil? (:ui/fetch-state current-user)))
         (ui-page-router page-router)
         (ui-loading))))))
