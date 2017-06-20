(ns styles.components
  (:require [styles.constants :as constants]
            [styles.util :refer [gradient steps make-modifiers width-xs width-sm width-md width-lg]]
            [garden.units :refer [px em percent ms vh vw]]))

(def app
  [:.c-app {:overflow :auto}])

(def page
  [:.c-page {:margin-top (-> constants/spacing :xx-large px)
             :margin-left :auto
             :margin-right :auto
             :margin-bottom (-> constants/spacing :large px)
             :width (px 1000)}
   (width-xs {:margin-top (-> constants/spacing :large px)
              :width (percent 100)})
   (width-sm {:margin-top (-> constants/spacing :x-large px)
              :width (px 480)})
   (width-md {:width (px 750)})])

(def text
  [:.c-text {:font-family "'Raleway', Arial"
             :font-weight 300
             :color (:dark-grey constants/color)
             :font-size (-> constants/paragraph :medium px)
             :line-height 1.8
             :text-decoration :none}


   [:&--link {:color (:grapefruit constants/color)
              :font-weight 400
              :text-shadow [[(em 0.03) 0 (:white constants/color)]
                            [(em -0.03) 0 (:white constants/color)]
                            [0 (em 0.03) (:white constants/color)]
                            [0 (em -0.03) (:white constants/color)]
                            [(em 0.06) 0 (:white constants/color)]
                            [(em -0.06) 0 (:white constants/color)]
                            [(em 0.09) 0 (:white constants/color)]
                            [(em -0.09) 0 (:white constants/color)]
                            [(em 0.12) 0 (:white constants/color)]
                            [(em -0.12) 0 (:white constants/color)]
                            [(em 0.15) 0 (:white constants/color)]
                            [(em -0.15) 0 (:white constants/color)]]
              :background-image [[(gradient (:white constants/color) (:white constants/color))]
                                 [(gradient (:white constants/color) (:white constants/color))]
                                 [(gradient (:grapefruit constants/color) (:grapefruit constants/color))]]
              :background-size [[(em 0.05) (px 1)]
                                [(em 0.05) (px 1)]
                                [(px 1) (px 1)]]
              :background-repeat [:no-repeat :no-repeat :repeat-x]
              :background-position [[(percent 0) (percent 90)]
                                    [(percent 100) (percent 90)]
                                    [(percent 0) (percent 80)]]}
    [:&:hover {:cursor :pointer}]]

   [:&--ellipsis {:white-space :nowrap
                  :overflow :hidden
                  :text-overflow :ellipsis}]

   [:&--align-center {:text-align :center}]

   [:&--font-weight-bold {:font-weight 400}]

   (make-modifiers [:&--color {:color [constants/color]}])
   (make-modifiers [:&--heading {:font-size [constants/heading px]}])
   (make-modifiers [:&--paragraph {:font-size [constants/paragraph px]}])
   (make-modifiers [:&--padding {:padding [constants/spacing px]}])
   (make-modifiers [:&--padding-top {:padding-top [constants/spacing px]}])
   (make-modifiers [:&--padding-bottom {:padding-bottom [constants/spacing px]}])
   (make-modifiers [:&--padding-left {:padding-left [constants/spacing px]}])
   (make-modifiers [:&--padding-right {:padding-right [constants/spacing px]}])])

(def icon
  [:.c-icon {:font-family "'icomoon'"
             :font-style :normal
             :font-weight :normal
             :font-variant :normal
             :text-transform :none
             :-webkit-font-smoothing :antialiased
             :-moz-osx-font-smoothing :grayscale
             :text-decoration :none}

   (make-modifiers [:&--color {:color [constants/color]}])
   (make-modifiers [:&--heading {:font-size [constants/heading px]}])
   (make-modifiers [:&--paragraph {:font-size [constants/paragraph px]}])
   (make-modifiers [:&--padding {:padding [constants/spacing px]}])
   (make-modifiers [:&--padding-top {:padding-top [constants/spacing px]}])
   (make-modifiers [:&--padding-bottom {:padding-bottom [constants/spacing px]}])
   (make-modifiers [:&--padding-left {:padding-left [constants/spacing px]}])
   (make-modifiers [:&--padding-right {:padding-right [constants/spacing px]}]) 

   [:&--question-circle:before {:content "'\\e87d'"}]
   [:&--arrow-left:before {:content "'\\e879'"}]
   [:&--moon:before {:content "'\\e808'"}]
   [:&--film-play:before {:content "'\\e824'"}]
   [:&--text-align-right:before {:content "'\\e89a'"}]
   [:&--google-plus:before {:content "'\\ea8b'"}]
   [:&--graduation-hat:before {:content "'\\e821'"}]
   [:&--printer:before {:content "'\\e81c'"}]
   [:&--dropbox:before {:content "'\\eaae'"}]
   [:&--pie-chart:before {:content "'\\e842'"}]
   [:&--bold:before {:content "'\\e893'"}]
   [:&--keyboard:before {:content "'\\e837'"}]
   [:&--paperclip:before {:content "'\\e819'"}]
   [:&--star-empty:before {:content "'\\e816'"}]
   [:&--bicycle:before {:content "'\\e850'"}]
   [:&--linkedin:before {:content "'\\eaca'"}]
   [:&--frame-contract:before {:content "'\\e88d'"}]
   [:&--tablet:before {:content "'\\e83b'"}]
   [:&--pointer-down:before {:content "'\\e8a8'"}]
   [:&--alarm:before {:content "'\\e858'"}]
   [:&--arrow-right:before {:content "'\\e87a'"}]
   [:&--volume-low:before {:content "'\\e85c'"}]
   [:&--envelope:before {:content "'\\e818'"}]
   [:&--github:before {:content "'\\eab0'"}]
   [:&--home:before {:content "'\\e800'"}]
   [:&--heart:before {:content "'\\e813'"}]
   [:&--youtube:before {:content "'\\ea9d'"}]
   [:&--chevron-left:before {:content "'\\e875'"}]
   [:&--inbox:before {:content "'\\e81a'"}]
   [:&--pointer-right:before {:content "'\\e8a7'"}]
   [:&--sad:before {:content "'\\e855'"}]
   [:&--bookmark:before {:content "'\\e829'"}]
   [:&--cart:before {:content "'\\e82e'"}]
   [:&--screen:before {:content "'\\e839'"}]
   [:&--car:before {:content "'\\e84e'"}]
   [:&--direction-rtl:before {:content "'\\e8a1'"}]
   [:&--pointer-left:before {:content "'\\e8a9'"}]
   [:&--thumbs-down:before {:content "'\\e86e'"}]
   [:&--facebook:before {:content "'\\ea90'"}]
   [:&--chevron-down:before {:content "'\\e874'"}]
   [:&--chevron-right:before {:content "'\\e876'"}]
   [:&--arrow-up:before {:content "'\\e877'"}]
   [:&--poop:before {:content "'\\e806'"}]
   [:&--store:before {:content "'\\e82d'"}]
   [:&--exit:before {:content "'\\e820'"}]
   [:&--indent-increase:before {:content "'\\e89d'"}]
   [:&--license:before {:content "'\\e822'"}]
   [:&--cloud-check:before {:content "'\\e80d'"}]
   [:&--construction:before {:content "'\\e841'"}]
   [:&--chevron-up-circle:before {:content "'\\e887'"}]
   [:&--cross-circle:before {:content "'\\e880'"}]
   [:&--chevron-down-circle:before {:content "'\\e888'"}]
   [:&--arrow-down:before {:content "'\\e878'"}]
   [:&--phone:before {:content "'\\e831'"}]
   [:&--page-break:before {:content "'\\e8a2'"}]
   [:&--highlight:before {:content "'\\e897'"}]
   [:&--redo:before {:content "'\\e861'"}]
   [:&--wheelchair:before {:content "'\\e851'"}]
   [:&--paypal:before {:content "'\\ead8'"}]
   [:&--eye:before {:content "'\\e81b'"}]
   [:&--twitter:before {:content "'\\ea96'"}]
   [:&--cloud:before {:content "'\\e809'"}]
   [:&--neutral:before {:content "'\\e856'"}]
   [:&--shirt:before {:content "'\\e82c'"}]
   [:&--flag:before {:content "'\\e817'"}]
   [:&--direction-ltr:before {:content "'\\e8a0'"}]
   [:&--vimeo:before {:content "'\\eaa0'"}]
   [:&--book:before {:content "'\\e828'"}]
   [:&--arrow-down-circle:before {:content "'\\e884'"}]
   [:&--drop:before {:content "'\\e804'"}]
   [:&--camera-video:before {:content "'\\e825'"}]
   [:&--move:before {:content "'\\e87b'"}]
   [:&--bubble:before {:content "'\\e83f'"}]
   [:&--power-switch:before {:content "'\\e83e'"}]
   [:&--history:before {:content "'\\e863'"}]
   [:&--cloud-sync:before {:content "'\\e80c'"}]
   [:&--leaf:before {:content "'\\e849'"}]
   [:&--pointer-up:before {:content "'\\e8a6'"}]
   [:&--text-align-left:before {:content "'\\e898'"}]
   [:&--menu-circle:before {:content "'\\e87e'"}]
   [:&--bug:before {:content "'\\e869'"}]
   [:&--chart-bars:before {:content "'\\e843'"}]
   [:&--sort-alpha-asc:before {:content "'\\e8a3'"}]
   [:&--gift:before {:content "'\\e844'"}]
   [:&--star:before {:content "'\\e814'"}]
   [:&--hand:before {:content "'\\e8a5'"}]
   [:&--music-note:before {:content "'\\e823'"}]
   [:&--calendar-full:before {:content "'\\e836'"}]
   [:&--chevron-up:before {:content "'\\e873'"}]
   [:&--volume-high:before {:content "'\\e85a'"}]
   [:&--briefcase:before {:content "'\\e84c'"}]
   [:&--hourglass:before {:content "'\\e85f'"}]
   [:&--mustache:before {:content "'\\e857'"}]
   [:&--sync:before {:content "'\\e862'"}]
   [:&--train:before {:content "'\\e84f'"}]
   [:&--magnifier:before {:content "'\\e86f'"}]
   [:&--indent-decrease:before {:content "'\\e89e'"}]
   [:&--underline:before {:content "'\\e895'"}]
   [:&--volume:before {:content "'\\e85d'"}]
   [:&--enter:before {:content "'\\e81f'"}]
   [:&--smartphone:before {:content "'\\e83a'"}]
   [:&--file-empty:before {:content "'\\e81d'"}]
   [:&--pencil:before {:content "'\\e802'"}]
   [:&--linearicons:before {:content "'\\e846'"}]
   [:&--text-format:before {:content "'\\e890'"}]
   [:&--enter-down:before {:content "'\\e867'"}]
   [:&--bullhorn:before {:content "'\\e859'"}]
   [:&--warning:before {:content "'\\e87c'"}]
   [:&--smile:before {:content "'\\e854'"}]
   [:&--line-spacing:before {:content "'\\e89c'"}]
   [:&--dinner:before {:content "'\\e847'"}]
   [:&--coffee-cup:before {:content "'\\e848'"}]
   [:&--undo:before {:content "'\\e860'"}]
   [:&--list:before {:content "'\\e872'"}]
   [:&--diamond:before {:content "'\\e845'"}]
   [:&--cloud-upload:before {:content "'\\e80a'"}]
   [:&--volume-medium:before {:content "'\\e85b'"}]
   [:&--checkmark-circle:before {:content "'\\e87f'"}]
   [:&--link:before {:content "'\\e86b'"}]
   [:&--text-format-remove:before {:content "'\\e891'"}]
   [:&--strikethrough:before {:content "'\\e896'"}]
   [:&--picture:before {:content "'\\e827'"}]
   [:&--laptop:before {:content "'\\e83c'"}]
   [:&--mic:before {:content "'\\e85e'"}]
   [:&--star-half:before {:content "'\\e815'"}]
   [:&--earth:before {:content "'\\e853'"}]
   [:&--exit-up:before {:content "'\\e868'"}]
   [:&--thumbs-up:before {:content "'\\e86d'"}]
   [:&--sun:before {:content "'\\e807'"}]
   [:&--code:before {:content "'\\e86a'"}]
   [:&--file-add:before {:content "'\\e81e'"}]
   [:&--frame-expand:before {:content "'\\e88c'"}]
   [:&--plus-circle:before {:content "'\\e881'"}]
   [:&--instagram:before {:content "'\\ea92'"}]
   [:&--camera:before {:content "'\\e826'"}]
   [:&--magic-wand:before {:content "'\\e803'"}]
   [:&--pilcrow:before {:content "'\\e89f'"}]
   [:&--database:before {:content "'\\e80e'"}]
   [:&--map-marker:before {:content "'\\e833'"}]
   [:&--apartment:before {:content "'\\e801'"}]
   [:&--arrow-right-circle:before {:content "'\\e886'"}]
   [:&--text-align-center:before {:content "'\\e899'"}]
   [:&--layers:before {:content "'\\e88e'"}]
   [:&--chevron-left-circle:before {:content "'\\e889'"}]
   [:&--spell-check:before {:content "'\\e838'"}]
   [:&--arrow-left-circle:before {:content "'\\e885'"}]
   [:&--cog:before {:content "'\\e810'"}]
   [:&--clock:before {:content "'\\e864'"}]
   [:&--google:before {:content "'\\ea88'"}]
   [:&--tag:before {:content "'\\e82f'"}]
   [:&--text-align-justify:before {:content "'\\e89b'"}]
   [:&--arrow-up-circle:before {:content "'\\e883'"}]
   [:&--funnel:before {:content "'\\e88f'"}]
   [:&--laptop-phone:before {:content "'\\e83d'"}]
   [:&--rocket:before {:content "'\\e84b'"}]
   [:&--dice:before {:content "'\\e812'"}]
   [:&--crop:before {:content "'\\e88b'"}]
   [:&--text-size:before {:content "'\\e892'"}]
   [:&--italic:before {:content "'\\e894'"}]
   [:&--circle-minus:before {:content "'\\e882'"}]
   [:&--location:before {:content "'\\e835'"}]
   [:&--cloud-download:before {:content "'\\e80b'"}]
   [:&--download:before {:content "'\\e865'"}]
   [:&--pushpin:before {:content "'\\e832'"}]
   [:&--lighter:before {:content "'\\e805'"}]
   [:&--user:before {:content "'\\e82a'"}]
   [:&--unlink:before {:content "'\\e86c'"}]
   [:&--heart-pulse:before {:content "'\\e840'"}]
   [:&--chevron-right-circle:before {:content "'\\e88a'"}]
   [:&--map:before {:content "'\\e834'"}]
   [:&--bus:before {:content "'\\e84d'"}]
   [:&--phone-handset:before {:content "'\\e830'"}]
   [:&--cross:before {:content "'\\e870'"}]
   [:&--trash:before {:content "'\\e811'"}]
   [:&--menu:before {:content "'\\e871'"}]
   [:&--upload:before {:content "'\\e866'"}]
   [:&--paw:before {:content "'\\e84a'"}]
   [:&--lock:before {:content "'\\e80f'"}]
   [:&--select:before {:content "'\\e852'"}]
   [:&--users:before {:content "'\\e82b'"}]
   [:&--sort-amount-asc:before {:content "'\\e8a4'"}]])

(def still-mascot
  [:.c-still-mascot {:width (-> constants/mascot :width px)
                     :height (-> constants/mascot :height px)}])

(def animated-mascot
  [:.c-animated-mascot {:width (-> constants/mascot :width px)
                        :height (-> constants/mascot :height px)
                        :overflow :hidden}
   [:&__animator {:width (-> (* (:animation-count constants/mascot) (:width constants/mascot)) px)
                  :animation-name :mascot
                  :animation-duration (ms 5000)
                  :animation-timing-function (steps 1)
                  :animation-iteration-count :infinite}]])
