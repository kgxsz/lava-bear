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

(def raleway
  (at-font-face {:font-family "'Raleway'"
                 :font-weight 300
                 :font-style :normal
                 :src "local('Raleway Light'),
                       local('Raleway-Light'),
                       url(https://fonts.gstatic.com/s/raleway/v11/-_Ctzj9b56b8RgXW8FAriQzyDMXhdD8sAj6OAJTFsBI.woff2) format('woff2')"
                 :unicode-range "U+0000-00FF, U+0131, U+0152-0153, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC, U+2212, U+2215"}))
