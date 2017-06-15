(ns styles.main
  (:require [styles.animations :as animations]
            [styles.components :as components]
            [styles.layouts :as layouts]
            [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-font-face]]
            [garden.units :refer [px percent ms vh vw]]
            [com.stuartsierra.component :as component]))

(defstyles main
   ;; Meyer reset
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

  ;; TODO - extract into own file
  ;; TODO - can I bring the google font in like this? https://fonts.googleapis.com/css?family=Fira+Mono
  ;; fonts
  (at-font-face {:font-family "'icomoon'"
                 :font-weight :normal
                 :font-style :normal
                 :src "url('/fonts/icomoon.eot?r0cvwu#iefix') format('embedded-opentype'),
                        url('/fonts/icomoon.woff2?r0cvwu') format('woff2'),
                        url('/fonts/icomoon.ttf?r0cvwu') format('truetype'),
                        url('/fonts/icomoon.woff?r0cvwu') format('woff'),
                        url('/fonts/icomoon.svg?r0cvwu#icomoon') format('svg')"})

  ;; animations
  animations/mascot

  ;; layouts
  layouts/box

  ;; components
  components/app
  components/page
  components/text
  components/icon
  components/mascot

  )
