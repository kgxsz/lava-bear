(ns client.ui
  (:require [client.navigation :as n]
            [shared.util :as util]
            [om.next :as om :refer-macros [defui]]
            [om-tools.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [untangled.client.core :as uc]
            [untangled.client.data-fetch :as ud]
            [untangled.client.mutations :as um]))

(defui ^:once StillMascot
  Object
  (render [this]
    (let [{:keys []} (om/props this)]
      (dom/div
       {:class (util/bem [:c-still-mascot])}
       (html (util/embed-svg "still-mascot.svg"))))))

(def ui-still-mascot (om/factory StillMascot))

(defui ^:once AnimatedMascot
  Object
  (render [this]
    (let [{:keys []} (om/props this)]
      (dom/div
       {:class (util/bem [:c-animated-mascot])}
       (dom/div
        {:class (util/bem [:c-animated-mascot__animator])}
        (html (util/embed-svg "animated-mascot.svg")))))))

(def ui-animated-mascot (om/factory AnimatedMascot))

(defui ^:once Loading
  Object
  (render [this]
    (dom/div
     {:class (util/bem :c-page)}
     (dom/div
      {:class (util/bem [:l-box #{:justify-center}])}
      (ui-still-mascot))
     (dom/div
      {:class (util/bem [:l-box #{:justify-center :margin-top-x-large}])}
      (dom/span
       {:class (util/bem [:c-text #{:paragraph-large}])}
       "loading")))))

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
        can-redirect-to-home? (n/navigate-internal this {:handler :home :replace? true}))))

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
         (dom/div
          {:class (util/bem :c-page)}
          (dom/div
           {:class (util/bem [:l-box #{:justify-center}])}
           (ui-still-mascot))

          (dom/div
           {:class (util/bem [:l-box #{:col :align-center}])}
           (dom/div
            {:class (util/bem [:l-box #{:margin-top-large}])}
            (dom/span
             {:class (util/bem [:c-text #{:heading-medium}])}
             "The heck?"))
           (dom/div
            {:class (util/bem [:l-box #{:col :align-center :margin-top-medium}])}
            (dom/span
             {:class (util/bem [:c-text])}
             "Something is broken!")
            (dom/span
             {:class (util/bem [:c-text])}
             "I bet you're really angry."))
           (dom/div
            {:class (util/bem [:l-box #{:margin-top-medium}])}
            (dom/span
             {:class (util/bem [:c-text #{:link :color-grapefruit}])
              :on-click #(n/navigate-internal this {:handler :home})}
             "speak to a manager"))))

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
        (n/navigate-external this {:url "https://www.facebook.com/v2.9/dialog/oauth"
                                   :query-params {:client_id client-id
                                                  :state id
                                                  :scope scope
                                                  :redirect_uri redirect-url}}))))

  (render [this]
    (let [{:keys [current-user auth-attempt]} (om/props this)
          {:keys [first-name]} current-user
          can-initialise-auth-attempt? (nil? auth-attempt)]
      (dom/div
       {:class (util/bem :c-page)}
       (dom/div
        {:class (util/bem [:l-box #{:justify-center}])}
        (ui-animated-mascot))

       (if (:user-id current-user)
         (dom/div
          {:class (util/bem [:l-box #{:col :align-center}])}
          (dom/div
           {:class (util/bem [:l-box #{:margin-top-large}])}
           (dom/span
            {:class (util/bem [:c-text #{:heading-medium}])}
            "Whoops!"))
          (dom/div
           {:class (util/bem [:l-box #{:col :align-center :margin-top-medium}])}
           (dom/span
            {:class (util/bem [:c-text])}
            first-name ", it looks like you're not on")
           (dom/span
            {:class (util/bem [:c-text])}
            "our guest list. What a shame. Bye."))
          (dom/div
           {:class (util/bem [:l-box #{:margin-top-medium}])}
           (dom/span
            {:class (util/bem [:c-text #{:link}])
             :on-click #(n/navigate-external this {:url "https://omfgdogs.com"})}
            "look at dogs instead")))

         (dom/div
          {:class (util/bem [:l-box #{:col :align-center}])}
          (dom/div
           {:class (util/bem [:l-box #{:margin-top-large}])}
           (dom/span
            {:class (util/bem [:c-text #{:heading-medium}])}
            "Hello!"))
          (dom/div
           {:class (util/bem [:l-box #{:col :align-center :margin-top-medium}])}
           (dom/span
            {:class (util/bem [:c-text])}
            "My name is Keigo.")
           (dom/span
            {:class (util/bem [:c-text])}
            "I build things."))

          (dom/div
           {:class (util/bem [:l-box #{:margin-top-medium}])}
           (dom/span
            {:class (util/bem [:c-text #{:link}])
             :on-click #(let [tempid (om/tempid)]
                          (when can-initialise-auth-attempt?
                            (om/transact! this `[(app/initialise-auth-attempt {:id ~tempid})
                                                 (untangled/load {:query [(:auth-attempt {:id ~tempid})]})])))}
            (cond
              auth-attempt "signing in"
              :else "sign in")))))))))

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
     {:class (util/bem :c-page)}
     (dom/div
      {:class (util/bem [:l-box #{:justify-center}])}
      (ui-animated-mascot))

     (dom/div
      {:class (util/bem [:l-box #{:col :align-center}])}
      (dom/div
       {:class (util/bem [:l-box #{:margin-top-large}])}
       (dom/span
        {:class (util/bem [:c-text #{:heading-medium}])}
        "You're lost!"))
      (dom/div
       {:class (util/bem [:l-box #{:col :align-center :margin-top-medium}])}
       (dom/span
        {:class (util/bem [:c-text])}
        "This page doesn't even exist.")
       (dom/span
        {:class (util/bem [:c-text])}
        "It's okay to feel scared."))
      (dom/div
       {:class (util/bem [:l-box #{:margin-top-medium}])}
       (dom/span
        {:class (util/bem [:c-text #{:link}])
         :on-click #(n/navigate-internal this {:handler :home})}
        "go someplace safe"))))))

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
       {:key react-key
        :class (util/bem [:c-app])}
       (if (and (seq navigation) (map? current-user) (nil? (:ui/fetch-state current-user)))
         (ui-page-router page-router)
         (ui-loading))))))
