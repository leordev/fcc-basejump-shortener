(ns shortener.core
  (:require [environ.core :refer [env]]
            [cheshire.core :as json]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]
            [ring.util.response :refer [redirect response]]
            [compojure.handler :refer [site]]
            [clojure.java.jdbc :as db]
            [compojure.core :refer [GET defroutes]])
  (import org.apache.commons.validator.UrlValidator)
  (:gen-class))

;; Environment Variables
(defn get-env-port
  "detect PORT environment variable"
  []
  (if-let [port (env :port)]
    (do (prn "Environment variable PORT detected: " port)
        (Integer. port))
    (do (prn "No-Environment variable PORT, setting default port as 8080")
        8080)))
(def port (get-env-port))

(defn get-env-db-url
  "detect DATABASE_URL environment variable"
  []
  (if-let [url (or (env :database-url) (env :cleardb-database-url)) ]
    (do (prn "Environment variable DATABASE_URL detected: " url)
        url)
    (do (prn (str "No-Environment variable DATABASE_URL, setting default url as "
                  "'jdbc:mysql://localhost:3306/fcc_shortener?user=root'"))
        "jdbc:mysql://localhost:3306/fcc_shortener?user=root")))
(def db-url (get-env-db-url))

(defn in-prod?
  "verifies if it's in production mode (environment variable PRODUCTION)"
  []
  (if-let [production (env :production)]
    (do (prn "Production Mode ON, environment variable PRODUCTION=" production)
        true)
    (do (prn "No-Environment variable PRODUCTION, production mode false")
        false)))

(defn bad-request
  "return status 400"
  [body]
  {:status 400
   :headers {}
   :body body})

(defn bad-response
  "return status 500"
  [body]
  {:status 500
   :headers {}
   :body body})

(defn error-response
  "return an error response with the given status and error message"
  [status message]
  {:status status
   :headers {}
   :body (json/generate-string {:error message})})

(defn validate-url
  "verify if its a valid url"
  [url]
  (let [validator (UrlValidator.)]
    (if (.isValid validator url)
      url nil)))

(defn insert-url!
  "creates and persists the url returning the id"
  [url]
  (:generated_key (first (db/insert! db-url
                                     :urls {:original_url url}))))

(defn get-url
  "find the url by the short id"
  [id]
  (db/query db-url
           ["select original_url from urls where id = ?" id]))

(defn short-url
      "generates the short url"
      [req]
      (if-let [url (validate-url (-> req :params :url))]
        (response (json/generate-string {:original_url url
                                             :short_url (insert-url! url)}))
        (error-response 400 (str "Wrong url format, make sure you have a valid protocol"
                                 " and real site."))))

(defn redirect-url
      "get the short and redirect to the original one"
      [id]
      (if-let [url (get-url id)]
        (redirect (:original_url (first url)))
        (error-response 404 (str "This url is not on database."))))

(defroutes all-routes
           (GET ["/new/:url", :url #".+" ] req (short-url req))
           (GET "/:id" [id] (redirect-url id)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [handler (if (not (in-prod?))
                  (reload/wrap-reload (site #'all-routes)) ;; only reload when dev
                  (site all-routes))]
    (run-server handler {:port port})
    (println "Running server on port " port)))
