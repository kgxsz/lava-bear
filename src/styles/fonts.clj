(ns styles.fonts
  (:require [garden.stylesheet :refer [at-font-face]]))

(def icomoon
  (at-font-face {:font-family "'icomoon'"
                 :font-weight :normal
                 :font-style :normal
                 :src "url('/fonts/icomoon.eot?r0cvwu#iefix') format('embedded-opentype'),
                       url('/fonts/icomoon.woff2?r0cvwu') format('woff2'),
                       url('/fonts/icomoon.ttf?r0cvwu') format('truetype'),
                       url('/fonts/icomoon.woff?r0cvwu') format('woff'),
                       url('/fonts/icomoon.svg?r0cvwu#icomoon') format('svg')"}))

(def roboto-regular
  (at-font-face {:font-family "'Roboto'"
                 :font-style :normal
                 :font-weight 400
                 :src "local('Roboto'),
                       local('Roboto-regular'),
                       url(https://fonts.gstatic.com/s/roboto/v16/oMMgfZMQthOryQo9n22dcuvvDin1pK8aKteLpeZ5c0A.woff2) format('woff2')"
                 :unicode-range "U+0000-00FF, U+0131, U+0152-0153, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC, U+2212, U+2215"}))

(def roboto-bold
  (at-font-face {:font-family "'Roboto'"
                 :font-style :normal
                 :font-weight 700
                 :src "local('Roboto Bold'),
                       local('Roboto-Bold'),
                       url(https://fonts.gstatic.com/s/roboto/v16/d-6IYplOFocCacKzxwXSOJBw1xU1rKptJj_0jans920.woff2) format('woff2')"
                 :unicode-range "U+0000-00FF, U+0131, U+0152-0153, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC, U+2212, U+2215"}))
