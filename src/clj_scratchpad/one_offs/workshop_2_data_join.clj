(ns clj-scratchpad.one-offs.workshop-2-data-join
  (:require [next.jdbc :as jdbc]
            [clj-http.client :as http]
            [clj-scratchpad.utils.password :as pass]
            [next.jdbc.sql :as sql]))

;; As the old monolith splits into shiny new microservices, it's
;; become harder to ask routine business intelligence questions.
;; 
;; After all, not all the data resides in one database together
;; anymore.
;; 
;; --------------------------------------------------------------------------
;; 
;; Here are some questions to answer:
;; 
;; What type of event is most effective at driving sales?
;;    - Please break this down by 20-year age groups (0-20, 21-40, 41-60, ...)
;;    - Please provide an answer in item quantity as well as gross proceeds
;; 
;; 
;; If the exercise is too easy, spend some time playing with streaming lazily
;; such that the full dataset never needs to sit in memory.


;;Password is visi0n
(def readonly-db
  {:dbtype "mysql" :host "workshop-db.wmatson.com"
   :user "read-only" :password (pass/get-password :orders-db/read-only)
   :dbname "orders"})

(def ds (jdbc/get-datasource readonly-db))

(def base-url "http://workshop.wmatson.com:8084/api/")

(comment
  
  ;; This query joins all the relevant tables to these questions
  (sql/query ds ["SELECT * FROM purchases p 
                  JOIN items i ON p.itemId = i.id
                  JOIN purchase_adjacent_events pae ON pae.purchaseId = p.id
                  LIMIT 10"])

  ;; Docs: http://workshop.wmatson.com:8084/api-docs
  ;; There are 1000 users total, the endpoint is paginated
  ;; with a max page size of 100
  (http/request {:url (str base-url "users")
                 :method :get
                 :as :json}))
