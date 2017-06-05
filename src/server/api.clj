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

(defmethod api-mutate 'app/initialise-auth-attempt [{:keys [config state]} k {tempid :id}]
  {:action (fn []
             (let [{:keys [database]} state
                   auth-attempt-id (java.util.UUID/randomUUID)]
               (swap! database assoc-in [:auth-attempts/by-id auth-attempt-id] {:id auth-attempt-id
                                                                                :initialised-at (tc/to-date (t/now))
                                                                                :client-id (get-in config [:auth :client-id])
                                                                                :redirect-url (get-in config [:auth :redirect-url])
                                                                                :scope (get-in config [:auth :scope])})
               {:tempids {tempid auth-attempt-id}}))})

(defmethod api-mutate 'app/finalise-auth-attempt [{:keys [request config state]} k {:keys [code] auth-attempt-id :id}]
  {:action (fn []
             (let [{{:keys [client-id client-secret redirect-url]} :auth} config
                   {:keys [sessions database]} state]
               (if (get-in @database [:auth-attempts/by-id auth-attempt-id])
                 (let [{:keys [status body error]} @(http/request {:url "https://graph.facebook.com/v2.9/oauth/access_token"
                                                                   :method :get
                                                                   :headers {"Accept" "application/json"}
                                                                   :query-params {"client_id" client-id
                                                                                  "client_secred" client-secret
                                                                                  "redirect_uri" redirect-url
                                                                                  "code" code}
                                                                   :timeout 5000})]

                   (log/info "confirming auth attempt" auth-attempt-id)

                   (if (= 200 status)
                     (let [{:keys [access-token]} (json/read-str body :key-fn csk/->kebab-case-keyword)
                           {:keys [status body error]} @(http/request {:url "https://graph.facebook.com/v2.9/me"
                                                                       :method :get
                                                                       :oauth-token access-token
                                                                       :headers {"Accept" "application/json"}
                                                                       :query-params {"fields" "email,first_name,last_name,picture.width(256).height(256)"}
                                                                       :timeout 5000})]

                       (if (= 200 status)

                         (let [{:keys [email picture first-name last-name] user-id :id} (json/read-str body :key-fn csk/->kebab-case-keyword)]
                           (log/infof "auth attempt %s succeeded, with user %s" auth-attempt-id user-id)
                           (swap! database #(-> %
                                                (update-in [:auth-attempts/by-id auth-attempt-id] merge {:success-at (tc/to-date (t/now))
                                                                                                         :user-id user-id})
                                                (update-in [:users/by-id user-id] merge {:user-id user-id
                                                                                         :email email
                                                                                         :first-name first-name
                                                                                         :last-name last-name
                                                                                         :avatar {:src (get-in picture [:data :url])}})))
                           (swap! sessions assoc (:session-key request) {:user-id user-id}))

                         (do (log/infof "api request failed for auth attempt %s, with status %s, and error %s " auth-attempt-id status body)
                             (swap! database assoc-in [:auth-attempts/by-id auth-attempt-id :failure-at] (tc/to-date (t/now))))))

                     (do (log/infof "access token request failed for auth attempt %s, with status %s, and error %s " auth-attempt-id status body)
                         (swap! database assoc-in [:auth-attempts/by-id auth-attempt-id :failure-at] (tc/to-date (t/now))))))

                 (log/info "unable to match auth attempt" auth-attempt-id))))})

(defmethod api-read :default [{:keys [ast] :as e} k p]
  (log/error "unrecognised query" (omp/ast->expr ast)))

(defmethod api-read :auth-attempt [{:keys [state]} k {auth-attempt-id :id}]
  (let [{:keys [database]} state]
    {:value (get-in @database [:auth-attempts/by-id auth-attempt-id] {})}))

(defmethod api-read :current-user [{:keys [request state]} k p]
  (let [{:keys [database sessions]} state
        {:keys [user-id]} (get @sessions (:session-key request))]
    (Thread/sleep 3000)
    {:value (get-in @database [:users/by-id user-id] {})}))
