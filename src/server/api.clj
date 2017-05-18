(ns server.api
  (:require [clj-time.core :as time]
            [om.next :as om]
            [om.next.impl.parser :as omp]
            [taoensso.timbre :as log]))

(defmulti api-mutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod api-mutate :default [e k p]
  (log/error "unrecognised mutation" k))

(defmethod api-mutate 'app/add-item [{:keys [config database]} k {:keys [id label]}]
  {:action (fn []
             (let [next-id (swap! (:last-id database) inc)]
               (swap! (:items database) conj {:id next-id :label label})
               {:tempids {id next-id}}))})

(defmethod api-mutate 'app/initialise-auth-attempt [{:keys [config database]} k {tempid :id}]
  {:action (fn []
             (let [id (java.util.UUID/randomUUID)]
               (swap! (:auth-attempts/by-id database)
                      assoc id {:id id
                                :initialised-at "now" #_(time/now)})
               {:tempids {tempid id}}))})

(defmethod api-mutate 'app/finalise-auth-attempt [{:keys [config database]} k {:keys [id code] :as p}]
  {:action (fn []
             ;; TODO - hit fb with code and everything, then create the session
             (let [auth-attempt (get @(:auth-attempts/by-id database) id)]
               (if auth-attempt
                 (swap! (:auth-attempts/by-id database)
                        assoc-in [id :finalised-at] "now + something" #_(time/now))
                 (log/warn "auth attempt doesn't exist yo"))))})

(defmethod api-read :default [{:keys [ast] :as e} k p]
  (log/error "unrecognised query" (omp/ast->expr ast)))

(defmethod api-read :auth-attempt [{:keys [config database]} k {:keys [id]}]
  (when-let [auth-attempt (get @(:auth-attempts/by-id database) id)]
    {:value {:id id
             :finalised-at (:finalised-at auth-attempt)
             :app-id (get-in config [:auth :app-id])
             :redirect-url (get-in config [:auth :redirect-url])
             :scope (get-in config [:auth :scope])}}))

(defmethod api-read :loaded-items [{:keys [config database query] :as e} k p]
  (Thread/sleep 1000)
  {:value (mapv #(select-keys % query) @(:items database))})
