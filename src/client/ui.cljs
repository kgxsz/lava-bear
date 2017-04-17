(ns client.ui
  (:require [om.next :as om :refer-macros [defui]]
            [untangled.client.core :as uc]
            [om.dom :as dom]))

(defui ^:once App
  static uc/InitialAppState
  (initial-state [this params] {:ui/react-key "App"
                                :some-data 42})
  static om/IQuery
  (query [this] [:ui/react-key :some-data])

  Object
  (render [this]
          (let [{:keys [ui/react-key some-data]} (om/props this)]
            (dom/div #js {:key react-key
                          :className "heading"}

                     (str "Hello world: " some-data)))))
