(ns client.mutations
  (:require [client.ui :as ui]
            [om.next :as om]
            [taoensso.timbre :as log]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as um]))

(defmethod um/mutate 'app/initialise-auth-attempt [{:keys [state]} _ _]
  {:remote true})

(defmethod um/mutate 'app/finalise-auth-attempt [{:keys [state]} _ _]
  {:remote true})

(defmethod um/mutate 'app/navigate-internally [{:keys [state]} _ {:keys [page handler route-params query-params]}]
  {:action (fn []
             (swap! state assoc
                    :navigation {:handler handler
                                 :route-params route-params
                                 :query-params query-params}
                    :page-router [page '_]))})
