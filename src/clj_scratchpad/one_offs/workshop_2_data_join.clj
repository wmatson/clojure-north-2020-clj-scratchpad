(ns clj-scratchpad.one-offs.workshop-2-data-join
  (:require [next.jdbc :as jdbc]
            [clj-http.client :as http]
            [clj-scratchpad.utils.password :as pass]
            [next.jdbc.sql :as sql]))

;;Password is visi0n
(def readonly-db
  {:dbtype "mysql" :host "workshop-db.wmatson.com"
   :user "read-only" :password (pass/get-password :orders-db/read-only)
   :dbname "orders"})

(def ds (jdbc/get-datasource readonly-db))

(def base-url "http://workshop.wmatson.com:8084/api/")

(comment
  (sql/query ds ["SELECT * FROM purchases p 
                  JOIN items i ON p.itemId = i.id
                  JOIN purchase_adjacent_events pae ON pae.purchaseId = p.id
                  LIMIT 10"])

  (http/request {:url (str base-url "favorite-function")
                 :method :get
                 :as :json}))

