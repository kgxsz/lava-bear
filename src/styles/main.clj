(ns styles.main
  (:require [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-font-face at-keyframes]]
            [garden.units :refer [px percent ms vh vw]]))

(defstyles main

  (at-keyframes :mascot-blink
                [:0% {:transform "translate(0px,0px)"}]
                [:100% {:transform "translate(0px,-1500px)"}])

  [:html :body :div :span :applet :object :iframe :h1 :h2 :h3 :h4 :h5 :h6 :p
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

  [:table {:border-collapse :collapse :border-spacing 0}]

  [:html {:height (percent 100)
          :overflow-y :scroll}]

  [:body {:height (percent 100)}]

  [:#js-app {:height (percent 100)
             :overflow :auto}]

  [:.c-mascot-container {:width (px 300)
                         :height (px 300)
                         :margin-top (vh 15)
                         :margin-left :auto
                         :margin-right :auto
                         :overflow :hidden}
   [:&__mascot
    [:&--sprites {:width (px 600)
                  :animation-name :mascot-blink
                  :animation-duration (ms 400)
                  :animation-timing-function "steps(5)"
                  :animation-direction :alternate
                  :animation-iteration-count :infinite}]]]

  [:.c-loader {:margin-top (px 30)
               :font-family "arial"
               :text-align :center}])
