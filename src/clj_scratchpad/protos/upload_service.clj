(ns clj-scratchpad.protos.upload-service
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [medley.core :as medley]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(defn- gen-ui []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<html>
          <body>
          <form action=\"/\" method=\"post\" enctype=\"multipart/form-data\">
          <input type=\"file\" name=\"file\" required/>
          <input type=\"submit\"/>
          </form>
          </body>
          </html>"})

(defn- parse-datum [datum]
  (-> datum
      (update :event-id #(Integer/parseInt %))
      (update :extra-1 #(Integer/parseInt %))))

(defn- parse-data [[headers & data]]
  (->> data
       ;; Purposefully caring about header order, as is the recommended Spring pattern
       (map zipmap (repeat [:event-id :event-name :event-type :extra-1]))
       (map parse-datum)))

(defn- validate-data [parsed-data]
  (->> parsed-data
       (map :event-id)
       frequencies
       (medley/filter-vals #(> % 1))
       empty?))

(defn- handle-upload [req]
  (let [file (get-in req [:params "file" :tempfile])]
    (with-open [reader (io/reader file)]
      (let [parsed (parse-data (csv/read-csv reader))]
        (if (validate-data parsed)
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body {:message "Uploaded!"
                  :data parsed}}
          {:status 400
           :headers {"Content-Type" "application/json"}
           :body {:message "Duplicate Ids Detected!"}})))))

(defn handler [req]
  (println (select-keys req [:request-method :uri]))
  (case (:request-method req)
    :get (gen-ui)
    :post (handle-upload req)
    {:body {:message (str (:request-method req) " not supported")}
     :status 500
     :headers {"Content-Type" "application/json"}}))

(def app (-> (wrap-reload #'handler)
             wrap-params
             wrap-multipart-params
             wrap-json-response))

(defn -main []
  (jetty/run-jetty app {:port 8080}))

(comment
  (def server (jetty/run-jetty app {:port 8080 :join? false}))
  (.stop server))