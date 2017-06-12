(ns shared.util
  (:require [hickory.core :as h]
            [om-tools.dom :as dom :include-macros true]))

#?(:clj (defn remove-svg-dimensions [v]
          ;; Remove the width and height attributes of the outer svg so
          ;; that the containing element can determine the dimensions
          (let [[_ attributes & rest] v]
            (vec (concat [:svg (dissoc attributes :width :height)] rest)))))

#?(:clj (defn conserve-svg-attributes [v]
          ;; When Hickory converts html to hiccup, it lower-cases the attributes. This is fine for html, but it's
          ;; problematic for svg which relies on camel cased attributes. This function walks a hiccup structure and
          ;; replaces any attributes whose camel casing should have been conserved. This is a hack.
          (if (vector? v)
            (let [[tag attributes & rest] v
                  replacements {:viewbox :viewBox}
                  updated-attributes (clojure.set/rename-keys attributes replacements)]
              (->> (map conserve-svg-attributes rest)
                   (concat [tag updated-attributes])
                   (vec)))
            v)))

#?(:clj (defmacro embed-svg [file-name]
          (let [hiccup (-> (str "resources/public/images/" file-name)
                           (slurp)
                           (h/parse-fragment)
                           (first)
                           (h/as-hiccup)
                           (conserve-svg-attributes)
                           (remove-svg-dimensions))]
            `~hiccup)))

(defn bem
 "Creates a class string from bem structured arguments.
  (bem :block
       :block__element
       :block__element--modifier
       [:block true]
       [:block__element true]
       [:block__element--modifier true]
       [:block__element #{:modifier-a :modifier-c}]
       [:block__element true #{:modifier-a :modifier-c}]
       [:block__element true #{:modifier-a [:modifier-c true]}]) "

  [& xs]

  (let [block-elements (filter keyword? xs)]
    (->> (for [vector (filter vector? xs)]
           (let [block-element (first (filter keyword? vector))
                 predicate (first (filter boolean? vector))
                 modifiers (first (filter set? vector))]
             (when-not (false? predicate)
               (cons
                (name block-element)
                (for [modifier modifiers]
                  (if (vector? modifier)
                    (when (second modifier)
                      (str (name block-element) "--" (name (first modifier))))
                    (str (name block-element) "--" (name modifier))))))))
         (concat (map name block-elements))
         (flatten)
         (remove nil?)
         (interpose " ")
         (apply str))))
