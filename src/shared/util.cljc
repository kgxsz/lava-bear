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
 "Creates a class string from bem structured arguments. Take multiple arguments in vectors.
  Each vector is composed of the block-elements keyword, then the optional modifiers then the
  optional set of breakpoints. The modifiers may be either a keyword, or a vector, which itself
  contains the modifer keyword, and a set of breakpoints. The top level breakpoints define at
  which screen widths the block-elements are active, the modifier level breakpoints do the same
  but only for that particular modifier. An empty set means that the block-element, or modifier
  will never be active. No set at all, however, is shorthand for #{:xs :sm :md :lg} for brevity.

  (bem [:block__element__element :modifier :modifier]
       [:block__element__element :modifier :modifier #{:xs :sm}]
       [:block__element__element :modifier [:modifier #{:md :lg}] #{:sm :md :lg}])"

  [& xs]

  (->> (for [x xs]
         (let [block-elements (first x)
               modifiers (filter (complement set?) (rest x))
               breakpoints (or (first (filter set? x)) #{:xs :sm :md :lg})]
           (when (seq breakpoints)
             (cons
              (name block-elements)
              (for [modifier modifiers]
                (if (vector? modifier)
                  (when (seq (second modifier))
                    (str (name block-elements) "--" (name (first modifier))))
                  (str (name block-elements) "--" (name modifier))))))))
       (flatten)
       (interpose " ")
       (apply str)))
