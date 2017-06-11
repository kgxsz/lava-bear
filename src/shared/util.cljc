(ns shared.util
  (:require [hickory.core :as h]
            [om-tools.dom :as dom :include-macros true]))

#?(:clj (defn conserve-svg-attributes [v]
          ;; When Hickory converts html to hiccup, it lower-cases the attributes. This is fine for html, but it's
          ;; problematic for svg which relies on camel cased attributes. This function walks a hiccup structure and
          ;; replaces any attributes whose camel casing should have been conserved. This is a hack.
          (if (vector? v)
            (let [[tag attributes & rest] v
                  replacements {:viewbox :viewBox}
                  updated-attributes (clojure.set/rename-keys attributes replacements)]
              (concat [tag updated-attributes] (mapv conserve-svg-attributes rest)))
            v)))

#?(:clj (defmacro embed-svg [file-name]
          (let [hiccup (-> (str "resources/public/images/" file-name)
                           (slurp)
                           (h/parse-fragment)
                           (first)
                           (h/as-hiccup)
                           (conserve-svg-attributes))]
            `~hiccup)))
