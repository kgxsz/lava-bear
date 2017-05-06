(ns server.api
  (:require [om.next :as om]
            [om.next.impl.parser :as omp]
            [taoensso.timbre :as log]))

(defmulti api-mutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod api-mutate :default [e k p]
  (log/error "unrecognised mutatuion " k))

(defmethod api-mutate 'app/add-item [{:keys [config database]} k {:keys [id label]}]
  {:action (fn []
             (let [next-id (swap! (:last-id database) inc)]
               (swap! (:items database) conj {:id next-id :label label})
               {:tempids {id next-id}}))})

(defmethod api-read :default [{:keys [ast] :as e} k p]
  (log/error "unrecognised query" (omp/ast->expr ast)))

(defmethod api-read :loaded-items [{:keys [config database query] :as e} k p]
  (Thread/sleep 1000)
  {:value (mapv #(select-keys % query) @(:items database))})

