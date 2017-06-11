(ns styles.main
  (:require [shared.util :refer [translate]]
            [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-font-face at-keyframes]]
            [garden.units :refer [px percent ms vh vw]]))

(def constants
  {:mascot-width 250
   :mascot-height 250
   :mascot-animation-count 2})

(def animation-keyframes
  [(let [y (- (:mascot-height constants))
         x (- (:mascot-width constants))]
     (at-keyframes :mascot
                   [:0% {:transform (translate 0 0)}]
                   [:1% {:transform (translate 0 y)}]
                   [:2% {:transform (translate 0 (* y 2))}]
                   [:3% {:transform (translate 0 (* y 3))}]
                   [:4% {:transform (translate 0 (* y 4))}]
                   [:5% {:transform (translate 0 (* y 3))}]
                   [:6% {:transform (translate 0 (* y 2))}]
                   [:7% {:transform (translate 0 y)}]
                   [:8% {:transform (translate 0 y)}]
                   [:40% {:transform (translate x y)}]
                   [:42% {:transform (translate x (* y 2))}]
                   [:44% {:transform (translate x (* y 3))}]
                   [:46% {:transform (translate x (* y 4))}]
                   [:48% {:transform (translate x (* y 3))}]
                   [:50% {:transform (translate x (* y 2))}]
                   [:52% {:transform (translate x y)}]
                   [:54% {:transform (translate 0 0)}]
                   [:100% {:transform (translate 0 0)}]))])

(def meyer-reset
  [[:html :body :div :span :applet :object :iframe :h1 :h2 :h3 :h4 :h5 :h6 :p
    :blockquote :pre :a :abbr :acronym :address :big :cite :code :del :dfn :em
    :img :ins :kbd :q :s :samp :small :strike :strong :sub :sup :tt :var :b :u
    :i :center :dl :dt :dd :ol :ul :li :fieldset :form :label :legend :table
    :caption :tbody :tfoot :thead :tr :th :td :article :aside :canvas :details
    :embed :figure :figcaption :footer :header :hgroup :menu :nav :output :ruby
    :section :summary :time :mark :audio :video
    {:margin 0 :padding 0 :border 0 :font-size (percent 100) :font :inherit :vertical-align :baseline}]
   [:* {:box-sizing :border-box}]
   [:article :aside :details :figcaption :figure :footer :header :hgroup :menu :nav :section
    {:display :block}]
   [:body {:line-height 1}]
   [:ol :ul {:list-style :none}]
   [:blockquote :q {:quotes :none}
    [:&:before :&:after {:content :none}]]
   [:table {:border-collapse :collapse :border-spacing 0}]])

(defstyles main
  meyer-reset
  animation-keyframes

  [:html {:height (percent 100)
          :overflow-y :scroll}]

  [:body {:height (percent 100)}]

  [:#js-app {:height (percent 100)
             :overflow :auto}]

  [:.c-mascot-container {:width (-> constants :mascot-width px)
                         :height (-> constants :mascot-height px)
                         :margin-top (vh 15)
                         :margin-left :auto
                         :margin-right :auto
                         :overflow :hidden}
   [:&__mascot
    [:&--sprites {:width (-> (* (:mascot-animation-count constants) (:mascot-width constants)) px)
                  :animation-name :mascot
                  :animation-duration (ms 5000)
                  :animation-timing-function "steps(1)"
                  :animation-iteration-count :infinite}]]]

  [:.c-loader {:margin-top (px 30)
               :font-family "arial"
               :text-align :center}])
