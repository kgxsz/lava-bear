(ns client.user
  (:require
   [devtools.core :as devtools]
   [untangled.client.logging :as log]
   [untangled.client.core :as uc]
   [client.ui :as ui]
   [client.core :as core]))

;; TODO - these guys could be in a start function
(enable-console-print!)
(log/set-level :debug)
#_(defonce cljs-build-tools (devtools/install!))

(swap! core/app uc/mount ui/Root "app")
