(ns user
  (:require
   [cljs.pprint :refer [pprint]]
   [devtools.core :as devtools]
   [untangled.client.logging :as log]
   [untangled.client.core :as uc]
   [app.ui :as ui]
   [app.core :as core]))

;; TODO - these guys could be in a start function
(enable-console-print!)
(log/set-level :debug)
#_(defonce cljs-build-tools (devtools/install!))

(swap! core/app uc/mount ui/Root "app")
