(ns client.core
  (:require [untangled.client.core :as uc]))

;; TODO - maybe this should be the system here, app is a system component, maybe it'll be called ui

(defonce app (atom (uc/new-untangled-client)))
