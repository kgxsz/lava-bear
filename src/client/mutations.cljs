(ns client.mutations
  (:require [client.ui :as ui]
            [om.next :as om]
            [untangled.client.core :as uc]
            [untangled.client.mutations :as um]))

(defmethod um/mutate 'app/update-auth-status [{:keys [state]} _ {:keys [auth-status]}]
  {:action (fn []
             (swap! state assoc :ui/auth-status auth-status))})

(defmethod um/mutate 'app/initialise-auth-attempt [{:keys [state]} _ {:keys [id]}]
  {:remote true})

(defmethod um/mutate 'app/finalise-auth-attempt [{:keys [state]} _ {:keys [code attempt-id]}]
  {:remote true})

(defmethod um/mutate 'app/navigate [{:keys [state]} _ {:keys [page handler route-params query-params]}]
  {:action (fn []
             (swap! state assoc
                    :navigation {:handler handler
                                 :route-params route-params
                                 :query-params query-params}
                    :page [page '_]))})
