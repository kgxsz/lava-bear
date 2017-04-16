(ns styles.main
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px]]))

(defstyles main
  [:body {:background-color "#EEE"}]
  [:.heading {:margin-top (px 250)
              :font-family "sans-serif"
              :color "#555"
              :text-align :center
              :font-size (px 45)
              :line-height 1.5}])
