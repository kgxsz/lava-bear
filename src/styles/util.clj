(ns styles.util
  (:require [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px percent ms vh vw]]))

(defn width-xs [styles]
  (at-media {:max-width (px 480)}
            [:& styles]))

(defn width-sm [styles]
  (at-media {:min-width (px 481) :max-width (px 768)}
            [:& styles]))

(defn width-md [styles]
  (at-media {:min-width (px 769) :max-width (px 1024)}
            [:& styles]))

(defn width-lg [styles]
  (at-media {:min-width (px 1025)}
            [:& styles]))

(defn gradient [left right]
  (str "linear-gradient("left", "right")"))

(defn translate [x y]
  (str "translate(" x "px," y "px)"))

(defn steps [n]
  (str "steps(" n ")"))

(defn make-modifiers
 "Creates a vector of modifiers, the modifier names will consist of the modifier
  and the constant keys. The unit function is used on each var of values if required."
  [[modifier properties]]
  (let [[property [values units]] (first properties)]
    (for [[k v] values]
      [(keyword (str (name modifier) "-" (name k)))
       {property (if units (units v) v)}])))
