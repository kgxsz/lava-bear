(ns styles.components
  (:require [styles.constants :as constants]
            [styles.util :refer [steps]]
            [garden.units :refer [px percent ms vh vw]]))

(def mascot-container
  [:.c-mascot-container {:width (-> constants/mascot :width px)
                         :height (-> constants/mascot :height px)
                         :overflow :hidden}
   [:&__mascot
    [:&--sprites {:width (-> (* (:animation-count constants/mascot) (:width constants/mascot)) px)
                  :animation-name :mascot
                  :animation-duration (ms 5000)
                  :animation-timing-function (steps 1)
                  :animation-iteration-count :infinite}]]])

(def loader
  [:.c-loader {:margin-top (px 30)
               :font-family "arial"
               :text-align :center}])
