(ns shared.util
  (:require [hickory.core :as h]
            [om-tools.dom :as dom :include-macros true]))

#?(:clj (defmacro embed-svg [file-name]
          (let [hiccup (h/as-hiccup (first (h/parse-fragment (slurp (str "resources/public/images/" file-name)))))]
            `~hiccup)))
