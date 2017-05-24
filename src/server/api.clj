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

(defmethod api-mutate 'app/add-item [{:keys [config state]} k {:keys [id label]}]
  {:action (fn []
             (let [{:keys [database]} state
                   next-id (-> @database
                               (get :items)
                               (last)
                               (get :id)
                               (inc))]
               (swap! database update :items conj {:id next-id :label label})
               {:tempids {id next-id}}))})

(defmethod api-mutate 'app/initialise-auth-attempt [{:keys [config state]} k {tempid :id}]
  {:action (fn []
             (let [{:keys [database]} state
                   id (java.util.UUID/randomUUID)]
               (swap! database assoc-in [:auth-attempts/by-id id] {:id id :initialised-at (tc/to-date (t/now))})
               {:tempids {tempid id}}))})

(defmethod api-mutate 'app/finalise-auth-attempt [{:keys [request config state]} k {:keys [id code]}]
  {:action (fn []
             (let [{{:keys [client-id client-secret redirect-url]} :auth} config
                   {:keys [sessions database]} state]
               (if (get-in @database [:auth-attempts/by-id id])
                 (let [{:keys [status body error]} @(http/request {:url "https://graph.facebook.com/v2.9/oauth/access_token"
                                                                   :method :get
                                                                   :headers {"Accept" "application/json"}
                                                                   :query-params {"client_id" client-id
                                                                                  "client_secret" client-secret
                                                                                  "redirect_uri" redirect-url
                                                                                  "code" code}
                                                                   :timeout 5000})]

                   (log/info "confirming auth attempt" id)

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
                           (log/infof "auth attempt %s succeeded, with user %s" id user-id)
                           (swap! database #(-> %
                                                (update-in [:auth-attempts/by-id id] merge {:success-at (tc/to-date (t/now))
                                                                                            :user-id user-id})
                                                (update-in [:users/by-id user-id] merge {:user-id user-id
                                                                                         :latest-auth-attempt id
                                                                                         :email email
                                                                                         :first-name first-name
                                                                                         :last-name last-name
                                                                                         :avatar {:src (get-in picture [:data :url])}})))
                           (swap! sessions assoc (:session-key request) {:user-id user-id}))

                         (do (log/infof "api request failed for auth attempt %s, with status %s, and error %s " id status body)
                             (swap! database assoc-in [:auth-attempts/by-id id :failure-at] (tc/to-date (t/now))))))

                     (do (log/infof "access token request failed for auth attempt %s, with status %s, and error %s " id status body)
                         (swap! database assoc-in [:auth-attempts/by-id id :failure-at] (tc/to-date (t/now))))))

                 (log/info "unable to match auth attempt" id))))})

(defmethod api-read :default [{:keys [ast] :as e} k p]
  (log/error "unrecognised query" (omp/ast->expr ast)))

(defmethod api-read :auth-attempt [{:keys [config state]} k {:keys [id]}]
  (let [{:keys [database]} state
        {:keys [initialised-at success-at failure-at user-id]} (get-in @database [:auth-attempts/by-id id])]
    {:value {:id id
             :success-at success-at
             :failure-at failure-at
             :user-id user-id
             :client-id (get-in config [:auth :client-id])
             :redirect-url (get-in config [:auth :redirect-url])
             :scope (get-in config [:auth :scope])}}))

(defmethod api-read :current-user [{:keys [request state]} k p]
  (let [{:keys [database sessions]} state
        {:keys [user-id]} (get @sessions (:session-key request))]
    {:value (get-in @database [:users/by-id user-id])}))

(defmethod api-read :items [{:keys [config state query] :as e} k p]
  (let [{:keys [database]} state]
    {:value (mapv #(select-keys % query) (get-in @database [:items]))}))
