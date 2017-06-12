(ns styles.animations
  (:require [styles.constants :as constants]
            [garden.stylesheet :refer [at-keyframes]]
            [styles.util :refer [translate]]))

(def mascot
  (let [y (- (:height constants/mascot))
        x (- (:width constants/mascot))]
    (at-keyframes :mascot
                  [[:0% {:transform (translate 0 0)}]
                   [:1% {:transform (translate 0 y)}]
                   [:2% {:transform (translate 0 (* y 2))}]
                   [:3% {:transform (translate 0 (* y 3))}]
                   [:4% {:transform (translate 0 (* y 4))}]
                   [:5% {:transform (translate 0 (* y 3))}]
                   [:6% {:transform (translate 0 (* y 2))}]
                   [:7% {:transform (translate 0 y)}]
                   [:8% {:transform (translate 0 y)}]
                   [:40% {:transform (translate x y)}]
                   [:41% {:transform (translate x (* y 2))}]
                   [:42% {:transform (translate x (* y 3))}]
                   [:43% {:transform (translate x (* y 4))}]
                   [:60% {:transform (translate x (* y 3))}]
                   [:61% {:transform (translate x (* y 2))}]
                   [:62% {:transform (translate x y)}]
                   [:63% {:transform (translate 0 0)}]
                   [:100% {:transform (translate 0 0)}]])))
