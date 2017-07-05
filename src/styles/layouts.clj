(ns styles.layouts
  (:require [styles.constants :as constants]
            [styles.util :refer [make-modifiers]]
            [garden.units :refer [px percent ms vh vw]]))

(def box
  [:.l-box {:display :flex}
   [:&--row {:flex-direction :row}]
   [:&--col {:flex-direction :column}]

   [:&--justify
    [:&-center {:justify-content :center}]
    [:&-space-between {:justify-content :space-between}]
    [:&-space-around {:justify-content :space-around}]
    [:&-start {:justify-content :flex-start}]
    [:&-end {:justify-content :flex-end}]]

   [:&--align
    [:&-center {:align-items :center}]
    [:&-baseline {:align-items :baseline}]
    [:&-start {:align-items :flex-start}]
    [:&-end {:align-items :flex-end}]]

   [:&--wrap {:flex-wrap :wrap}]
   [:&--wrap-reverse {:flex-wrap :wrap-reverse}]

   [:&__item
    [:&--grow-1 {:flex-grow 1}]
    [:&--grow-2 {:flex-grow 2}]
    [:&--grow-3 {:flex-grow 3}]
    [:&--basis-0 {:flex-basis 0}]]

   [:&--position
    [:&-top {:position :absolute
             :top 0}]
    [:&-bottom {:position :absolute
                :bottom 0}]
    [:&-left {:position :absolute
              :left 0}]
    [:&-right {:position :absolute
               :right 0}]]

   (make-modifiers [:&--background-color {:background-color [constants/color]}])
   (make-modifiers [:&--width {:width [constants/filling px]}])
   (make-modifiers [:&--width {:width [constants/proportion percent]}])
   (make-modifiers [:&--height {:height [constants/filling px]}])
   (make-modifiers [:&--height {:height [constants/proportion percent]}])
   (make-modifiers [:&--margin {:margin [constants/spacing px]}])
   (make-modifiers [:&--margin-top {:margin-top [constants/spacing px]}])
   (make-modifiers [:&--margin-bottom {:margin-bottom [constants/spacing px]}])
   (make-modifiers [:&--margin-left {:margin-left [constants/spacing px]}])
   (make-modifiers [:&--margin-right {:margin-right [constants/spacing px]}])
   (make-modifiers [:&--padding {:padding [constants/spacing px]}])
   (make-modifiers [:&--padding-top {:padding-top [constants/spacing px]}])
   (make-modifiers [:&--padding-bottom {:padding-bottom [constants/spacing px]}])
   (make-modifiers [:&--padding-left {:padding-left [constants/spacing px]}])
   (make-modifiers [:&--padding-right {:padding-right [constants/spacing px]}])])
