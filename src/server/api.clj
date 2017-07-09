(ns server.api
  (:require [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [clojure.data.json :as json]
            [camel-snake-kebab.core :as csk]
            [om.next :as om]
            [om.next.impl.parser :as omp]
            [org.httpkit.client :as http]
            [taoensso.timbre :as log]))

(defmulti api-mutate om/dispatch)
(defmulti api-read om/dispatch)

(defmethod api-mutate :default [e k p]
  (log/error "unrecognised mutation" k))

(defmethod api-read :default [{:keys [ast] :as e} k p]
  (log/error "unrecognised query" (omp/ast->expr ast)))
