(ns clj-scratchpad.one-offs.workshop-solution-2-data-join
  (:require [next.jdbc :as jdbc]
            [clj-http.client :as http]
            [clj-scratchpad.one-offs.workshop-solution-1-csv-upload :as exercise-1]
            [clj-scratchpad.utils.password :as pass]
            [medley.core :as medley]
            [next.jdbc.sql :as sql]))

;;Password is visi0n
(def readonly-db
  {:dbtype "mysql" :host "workshop-db.wmatson.com"
   :user "read-only" :password (pass/get-password :orders-db/read-only)
   :dbname "orders"})

(def ds (jdbc/get-datasource readonly-db))

(def base-url "http://workshop.wmatson.com:8084/api/")

(defn- coerce-id [event]
  (update event :event-id #(Integer/parseInt %)))

(defn gross-proceeds [{:keys [purchases/quantity items/cost]}]
  (* quantity cost))

(defn- get-users [page]

  (-> (http/request {:url (str base-url "users")
                     :method :get
                     :query-params {:skip (* 100 page)
                                    :limit page}
                     :as :json})
      :body
      :result))

(defn- join-data [purchase-rows users events]
  (-> (set purchase-rows)
      (clojure.set/join (set events) {:purchase_adjacent_events/eventId :event-id})
      (clojure.set/join (set users) {:purchases/userId :id})))

(comment
  (def purchase-rows
    (sql/query ds ["SELECT * FROM purchases p 
                   JOIN items i ON p.itemId = i.id
                   JOIN purchase_adjacent_events pae ON pae.purchaseId = p.id"]))

  (def events (->> (exercise-1/get-corrected-data)
                   (map coerce-id)))

  (def users (->> (range 10)
                  (mapcat get-users)))

  ;; Gross Proceeds
  (->> (join-data purchase-rows users events)
       (group-by (juxt #(quot (:age %) 20) :event-type))
       (medley/map-vals #(map gross-proceeds %))
       (medley/map-vals #(apply + %))
       (sort-by (comp - second)))

  ;; Quantity
  (->> (join-data purchase-rows users events)
       (group-by (juxt #(quot (:age %) 20) :event-type))
       (medley/map-vals #(map :purchases/quantity %))
       (medley/map-vals #(apply + %))
       (sort-by (juxt ffirst (comp - second))))

  ;; Docs: http://workshop.wmatson.com:8084/api-docs
  (http/request {:url (str base-url "users")
                 :method :get
                 :as :json}))
