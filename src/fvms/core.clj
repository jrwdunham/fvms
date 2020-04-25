(ns fvms.core
  "Fake Venue Management web service. Emulates a venue management web service
  that can process requests related to the creation of leads on venues/spaces.
  Constructs a simple in-memory state."
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [clojure.walk :as w]
            [cheshire.core :as ch]))

(def inquiries (atom {}))

(defn pp-str
  [x]
  (with-out-str (pprint/pprint x)))

(def json-header {"Content-Type" "application/json"})

(defn query-string->map
  [query-params]
  (try
    (->> (str/split query-params #"&")
         (map #(str/split % #"="))
         (into {}))
    (catch Exception _ {})))

(defn get-resource-id
  [uri resource-prefix]
  (let [resource-id (->> uri (drop (count resource-prefix)) (apply str))]
    (if (empty? (str/trim resource-id))
      nil
      resource-id)))

(def inquiries-prefix "/inquiries/")

(defn uri->kw
  [uri]
  (->> uri
       seq
       (#(if (= \/ (first uri)) (drop 1 %) %))
       (#(if (= \/ (last uri)) (drop-last %) %))
       (map #(if (= \/ %) \- %))
       (apply str)
       keyword))

(defmulti router
  (fn [{:keys [uri]}]
    (if (and (str/starts-with? uri inquiries-prefix)
             (get-resource-id uri inquiries-prefix))
      :inquiry
      (uri->kw uri))))

(defn filter-resources
  [resources filter-map]
  (->> resources
       (filter (fn [r]
                 (->> filter-map
                      (map (fn [[k v]] (= (get r k) v)))
                      (every? identity))))))

(defmethod router :default
  [{:keys [uri]}]
  {:status 200 :body uri})

(defmethod router :inquiries
  [{:keys [query-string]}]
  (let [query (if query-string (-> query-string query-string->map) {})]
    {:status 200
     :body (filter-resources (vals @inquiries) query)}))

(defmethod router :inquiry
  [{:keys [uri]}]
  (let [inquiry-id (get-resource-id uri inquiries-prefix)
        inquiry (get @inquiries inquiry-id
                     {:error true
                      :message
                      (format "There is no inquiry with id %s. The known IDs are:\n%s\n"
                              inquiry-id
                              (->> @inquiries
                                   keys
                                   (map str)
                                   (str/join "\n")))})]
    {:status (if (:error inquiry) 404 200)
     :body inquiry}))

(defmethod router :services-oauth2-token
  [_]
  {:status 200
   :body {:access_token "fakeaccesstoken"}})

(defn get-uuid
  [] (.toString (java.util.UUID/randomUUID)))

(defmethod router :services-apexrest-createinquiry
  [{:keys [body]}]
  (let [id (format "fake_delphi_inquiry_id_%s" (get-uuid))]
    (swap! inquiries assoc id (assoc body :inquiry-id id))
    {:status 201 :body id}))

(defmethod router :v1-leads-create.js
  [{:keys [body] :as r}]
  (let [id (format "fake_tripleseat_lead_id_%s"
                   (-> body
                       w/keywordize-keys
                       :lead
                       :additional_information))]
    (printf "Setting a new lead at ID %s.\n" id)
    (swap! inquiries assoc id (assoc body :lead-id id))
    {:status 201
     :body
     {:success_message "Lead submitted successfully!"
      :lead_id id}}))

(defn json-encode
  [handler]
  (fn [request]
    (let [response (handler request)]
      (merge response
             {:headers json-header
              :body (ch/generate-string (:body response))}))))

(defn json-decode
  [handler]
  (fn [request]
    (let [body (-> request
                   :body
                   (slurp :encoding "UTF-8")
                   ch/parse-string)]
      (handler (assoc request :body body)))))

(defn debug
  [handler]
  (fn [{:keys [uri request-method] :as request}]
    (log/infof "%s request to %s" request-method uri)
    (log/infof (pp-str @inquiries))
    (handler request)))

(def app (-> router
             (json-encode)
             (json-decode)))

