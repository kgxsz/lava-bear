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
    (dom/div
     {:class (util/bem [:c-animated-mascot])}
     (dom/div
      {:class (util/bem [:c-animated-mascot__animator])}
      (html (util/embed-svg "animated-mascot.svg"))))))

(def ui-animated-mascot (om/factory AnimatedMascot))

(defui ^:once AnimatedRoles
  Object
  (render [this]
    (dom/div
     {:class (util/bem [:c-animated-roles])}
     (dom/div
      {:class (util/bem [:c-animated-roles__animator])}
      (dom/div
       {:class (util/bem [:l-box :col])}
       (for [role ["frontendy." "backendy." "devopsy." "designy."]]
         (dom/div
          {:class (util/bem [:c-animated-roles__animator__frame])}
          (dom/div
           {:class (util/bem [:l-box :row :align-center :height-100 :padding-left-tiny])}
           (dom/span
            {:class (util/bem [:c-text :padding-right-xxx-small])}
            "I'm")
           (dom/span
            {:class (util/bem [:c-text :color-grapefruit])}
            role)))))))))

(def ui-animated-roles (om/factory AnimatedRoles))

(defui ^:once Loading
  Object
  (render [this]
    (dom/div
     {:class (util/bem [:c-page])}
     (dom/div
      {:class (util/bem [:l-box :justify-center])}
      (ui-still-mascot))
     (dom/div
      {:class (util/bem [:l-box :justify-center :margin-top-huge])}
      (dom/span
       {:class (util/bem [:c-text :paragraph-large])}
       "loading")))))

(def ui-loading (om/factory Loading))

(defui ^:once HomePage
  static uc/InitialAppState
  (initial-state [this params]
    {:id '_ :page :home-page})

  static om/IQuery
  (query [this]
    [:id :page [:current-user '_]])

  Object
  (render [this]
    (dom/div
      {:class (util/bem [:c-page])}
      (dom/div
       {:class (util/bem [:l-box :justify-center])}
       (ui-animated-mascot))
      (dom/div
       {:class (util/bem [:l-box :col :align-center])}
       (dom/div
        {:class (util/bem [:l-box :margin-top-xx-large])}
        (dom/span
         {:class (util/bem [:c-text :heading-medium :font-weight-bold])}
         "Hello!"))

       (dom/div
        {:class (util/bem [:l-box :col :align-center :margin-top-large])}
        (dom/span
         {:class (util/bem [:c-text])}
         "My name is Keigo.")
        (ui-animated-roles))

       (let [on-click #(n/navigate-externally this {:url "https://github.com/kgxsz"})]
         (dom/div
          {:class (util/bem [:l-box :row :align-baseline :margin-top-xxx-large :underlay])}
          (dom/span
           {:class (util/bem [:c-text :link :padding-right-large])
            :on-click on-click}
           "visit my")
          (dom/div
           {:class (util/bem [:l-box :row :align-center :overlay :position-top :position-bottom :position-right :clickable])
            :on-click on-click}
           (dom/span
            {:class (util/bem [:c-icon :github :paragraph-medium :padding-bottom-tiny :color-grapefruit])}))))))))

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
     {:class (util/bem [:c-page])}
     (dom/div
      {:class (util/bem [:l-box :justify-center])}
      (ui-animated-mascot))

     (dom/div
      {:class (util/bem [:l-box :col :align-center])}
      (dom/div
       {:class (util/bem [:l-box :margin-top-xx-large])}
       (dom/span
        {:class (util/bem [:c-text :heading-medium :font-weight-bold])}
        "You're lost!"))
      (dom/div
       {:class (util/bem [:l-box :col :align-center :margin-top-large])}
       (dom/span
        {:class (util/bem [:c-text])}
        "This page doesn't even exist.")
       (dom/span
        {:class (util/bem [:c-text])}
        "It's okay to feel scared."))
      (dom/div
       {:class (util/bem [:l-box :margin-top-xxx-large])}
       (dom/span
        {:class (util/bem [:c-text :link])
         :on-click #(n/navigate-internally this {:handler :home})}
        "go someplace safe"))))))

(def ui-unknown-page (om/factory UnknownPage))

(defui ^:once PageRouter
  static uc/InitialAppState
  (initial-state [this params]
    (uc/initial-state UnknownPage {}))

  static om/IQuery
  (query [this]
    {:home-page (om/get-query HomePage)
     :unknown-page (om/get-query UnknownPage)})

  static om/Ident
  (ident [this {:keys [page id]}]
    [page id])

  Object
  (render [this]
    (let [{:keys [page] :as props} (om/props this)]
      (case page
        :home-page (ui-home-page props)
        (ui-unknown-page props)))))

(def ui-page-router (om/factory PageRouter))

(defui ^:once App
  static uc/InitialAppState
  (initial-state [this params]
    {:ui/react-key :app
     :navigation nil
     :page-router (uc/initial-state PageRouter {})})

  static om/IQuery
  (query [this]
    [:ui/react-key
     :navigation
     {:page-router (om/get-query PageRouter)}])

  Object
  (render [this]
    (let [{:keys [ui/react-key navigation page-router]} (om/props this)]
      (dom/div
       {:key react-key
        :class (util/bem [:c-app])}
       (if (seq navigation)
         (ui-page-router page-router)
         (ui-loading))))))
