(ns styles.layouts
  (:require [styles.constants :as constants]
            [styles.util :refer [make-modifiers]]
            [garden.units :refer [px percent ms vh vw]]))

(def cell
  [:.l-cell
   [:&--justify
    [:&-center {:display :flex
                :flex-direction :row
                :justify-content :center}]
    [:&-start {:display :flex
               :flex-direction :row
               :justify-content :flex-start}]
    [:&-end {:display :flex
             :flex-direction :row
             :justify-content :flex-end}]]

   [:&--align
    [:&-center {:display :flex
                :flex-direction :row
                :align-items :center}]
    [:&-start {:display :flex
               :flex-direction :row
               :align-items :flex-start}]
    [:&-end {:display :flex
             :flex-direction :row
             :align-items :flex-end}]]

   (make-modifiers [:&--margin {:margin {:values constants/spacing :units px}}])
   (make-modifiers [:&--margin-top {:margin-top {:values constants/spacing :units px}}])
   (make-modifiers [:&--margin-bottom {:margin-bottom {:values constants/spacing :units px}}])
   (make-modifiers [:&--margin-left {:margin-left {:values constants/spacing :units px}}])
   (make-modifiers [:&--margin-right {:margin-right {:values constants/spacing :units px}}])

   (make-modifiers [:&--padding {:padding {:values constants/spacing :units px}}])
   (make-modifiers [:&--padding-top {:padding-top {:values constants/spacing :units px}}])
   (make-modifiers [:&--padding-bottom {:padding-bottom {:values constants/spacing :units px}}])
   (make-modifiers [:&--padding-left {:padding-left {:values constants/spacing :units px}}])
   (make-modifiers [:&--padding-right {:padding-right {:values constants/spacing :units px}}])

   (make-modifiers [:&--width {:width {:values constants/filling :units px}}])
   (make-modifiers [:&--width {:width {:values constants/proportion :units percent}}])

   (make-modifiers [:&--height {:height {:values constants/filling :units px}}])
   (make-modifiers [:&--height {:height {:values constants/proportion :units percent}}])

   (make-modifiers [:&--background-color {:background-color {:values constants/color}}])])

(def row
  [:.l-row
   [:&--justify
    [:&-center {:display :flex
                :flex-direction :row
                :justify-content :center}]
    [:&-start {:display :flex
               :flex-direction :row
               :justify-content :flex-start}]
    [:&-end {:display :flex
             :flex-direction :row
             :justify-content :flex-end}]]

   [:&--align
    [:&-center {:display :flex
                :flex-direction :row
                :align-items :center}]
    [:&-start {:display :flex
               :flex-direction :row
               :align-items :flex-start}]
    [:&-end {:display :flex
             :flex-direction :row
             :align-items :flex-end}]]

   (make-modifiers [:&--margin {:margin {:values constants/spacing :units px}}])
   (make-modifiers [:&--margin-top {:margin-top {:values constants/spacing :units px}}])
   (make-modifiers [:&--margin-bottom {:margin-bottom {:values constants/spacing :units px}}])
   (make-modifiers [:&--margin-left {:margin-left {:values constants/spacing :units px}}])
   (make-modifiers [:&--margin-right {:margin-right {:values constants/spacing :units px}}])

   (make-modifiers [:&--padding {:padding {:values constants/spacing :units px}}])
   (make-modifiers [:&--padding-top {:padding-top {:values constants/spacing :units px}}])
   (make-modifiers [:&--padding-bottom {:padding-bottom {:values constants/spacing :units px}}])
   (make-modifiers [:&--padding-left {:padding-left {:values constants/spacing :units px}}])
   (make-modifiers [:&--padding-right {:padding-right {:values constants/spacing :units px}}])

   (make-modifiers [:&--width {:width {:values constants/filling :units px}}])
   (make-modifiers [:&--width {:width {:values constants/proportion :units percent}}])

   (make-modifiers [:&--height {:height {:values constants/filling :units px}}])
   (make-modifiers [:&--height {:height {:values constants/proportion :units percent}}])

   (make-modifiers [:&--background-color {:background-color {:values constants/color}}])])
