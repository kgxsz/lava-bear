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
                                :initialised-at (tc/to-date (t/now))})
               {:tempids {tempid id}}))})

(defmethod api-mutate 'app/finalise-auth-attempt [{:keys [config database]} k {:keys [id code]}]
  {:action (fn []
             (if (get @(:auth-attempts/by-id database) id)
               (let [client-id (get-in config [:auth :client-id])
                     client-secret (get-in config [:auth :client-secret])
                     redirect-url (get-in config [:auth :redirect-url])
                     {:keys [status body error]} @(http/request {:url "https://graph.facebook.com/v2.9/oauth/access_token"
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
                         (swap! (:auth-attempts/by-id database) update-in [id] merge {:success-at (tc/to-date (t/now))
                                                                                      :user-id user-id})
                         (swap! (:users/by-id database) update-in [user-id] merge {:user-id user-id
                                                                                   :latest-auth-attempt id
                                                                                   :email email
                                                                                   :first-name first-name
                                                                                   :last-name last-name
                                                                                   :avatar {:src (get-in picture [:data :url])}}))

                       (do (log/infof "api request failed for auth attempt %s, with status %s, and error %s " id status body)
                           (swap! (:auth-attempts/by-id database) assoc-in [id :failure-at] (tc/to-date (t/now))))))

                   (do (log/infof "access token request failed for auth attempt %s, with status %s, and error %s " id status body)
                       (swap! (:auth-attempts/by-id database) assoc-in [id :failure-at] (tc/to-date (t/now))))))

               (log/info "unable to match auth attempt" id)))})

(defmethod api-read :default [{:keys [ast] :as e} k p]
  (log/error "unrecognised query" (omp/ast->expr ast)))

(defmethod api-read :auth-attempt [{:keys [config database]} k {:keys [id]}]
  (let [{:keys [initialised-at success-at failure-at user-id] :as auth-attempt} (get @(:auth-attempts/by-id database) id)]
    {:value {:id id
             :success-at success-at
             :failure-at failure-at
             :user-id user-id
             :client-id (get-in config [:auth :client-id])
             :redirect-url (get-in config [:auth :redirect-url])
             :scope (get-in config [:auth :scope])}}))

(defmethod api-read :user [{:keys [config database query]} k {:keys [user-id]}]
  {:value (get @(:users/by-id database) user-id)})

(defmethod api-read :hello [{:keys [request config state database query]} k _]
  (let [{:keys [session-key]} request
        {:keys [sessions]} state
        session (get @sessions session-key)]
    (log/warn "Exploring the current session")
    (log/warn (pr-str session))
    (swap! sessions assoc session-key {:a "a"})
    {:value {:a "d"}}))

(defmethod api-read :bye [{:keys [request config state database query]} k _]
  (let [{:keys [session-key]} request
        {:keys [sessions]} state]
    (swap! sessions dissoc session-key)
    {:value {:a "d"}}))

(defmethod api-read :loaded-items [{:keys [config database query] :as e} k p]
  (Thread/sleep 1000)
  {:value (mapv #(select-keys % query) @(:items database))})
