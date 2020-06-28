(ns clj-scratchpad.one-offs.tckt-101-populate-orders-db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [clj-scratchpad.utils.password :as pass]
            [clj-scratchpad.utils.csv :as csv]))

(defn- create-schema! [ds]
  (jdbc/execute! ds ["CREATE TABLE items ( 
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      cost FLOAT NOT NULL)"])

  (jdbc/execute! ds ["CREATE TABLE purchases ( 
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      userId INT NOT NULL,
                      itemId INT NOT NULL,
                      quantity INT NOT NULL)"])

  (jdbc/execute! ds ["CREATE TABLE purchase_adjacent_events (
                      purchaseId INT NOT NULL,
                      eventId INT NOT NULL,
                      direction enum('above','below'))"]))

(defn- create-items! [ds]
  (sql/insert-multi! ds :items
                     [:name :cost]
                     [["apple" 5]
                      ["orange" 4.7]
                      ["banana" 3.6]
                      ["cloth cap" 27]
                      ["morel mushroom" 10.5]
                      ["clay pot" 10.3]
                      ["shiny rock" 100]]))

(defn- create-read-user [ds]
  (jdbc/execute! ds ["create user 'read-only'@'%' identified by 'visi0n'"])
  (jdbc/execute! ds ["grant select on orders.* to 'read-only'@'%'"])
  (jdbc/execute! ds ["FLUSH PRIVILEGES"]))

(defn- populate-purchases! [ds purchases]
  (let [generated-ids (->> purchases
                           (csv/maps->csv-data [:user-id :item-id :quantity])
                           next
                           (sql/insert-multi! ds :purchases [:userid :itemid :quantity])
                           (map :GENERATED_KEY)
                           (map hash-map (repeat :purchase-id)))
        enriched-purchases (map merge purchases generated-ids)
        above-events (->> enriched-purchases
                          (map #(assoc % :direction "above"))
                          (csv/maps->csv-data [:purchase-id :above-event :direction])
                          next)
        below-events (->> enriched-purchases
                          (map #(assoc % :direction "below"))
                          (csv/maps->csv-data [:purchase-id :below-event :direction])
                          next)]
    (sql/insert-multi! ds :purchase_adjacent_events [:purchaseid :eventid :direction]
                       (concat above-events below-events))
    enriched-purchases))

(defn- gen-purchase [{:keys [user-ids event-ids item-ids]}]
  {:user-id (rand-nth user-ids)
   :item-id (rand-nth item-ids)
   :quantity (inc (rand-int 5))
   :above-event (rand-nth event-ids)
   :below-event (rand-nth event-ids)})

(defn- delete-schema! [ds]
  (jdbc/execute! ds ["DROP TABLE items"])
  (jdbc/execute! ds ["DROP TABLE purchases"])
  (jdbc/execute! ds ["DROP TABLE purchase_adjacent_events"]))

(defn- get-prop [prop default]
  (or (System/getProperty prop)
      (get (System/getenv) prop)
      default))

(defn -main [& args]
  (let [admin-pass (get-prop "MYSQL_ADMIN_PASSWORD" "clojure-north-2020!")
        readonly-pass (get-prop "MYSQL_READER_PASSWORD" "visi0n")
        mysql-host (get-prop "MYSQL_HOST" "mysql")
        db {:dbtype "mysql" :host mysql-host
            :user "root" :password admin-pass
            :dbname "orders"}
        ds (jdbc/get-datasource db)]
    (println "Creating Schema")
    (create-schema! ds)
    (println "Creating Read User")
    (create-read-user ds)
    (println "Creating Items")
    (create-items! ds)
    (println "Populating Orders")
    (let [possible-item-ids (map :items/id (sql/query ds ["SELECT id FROM items"]))
          article-ids [340 404 100 173]
          gen-args {:user-ids (range 1000)
                    :event-ids (apply concat (range 500) (repeat 50 article-ids))
                    :item-ids possible-item-ids}]
      (->> (repeatedly 300 #(gen-purchase gen-args))
           (populate-purchases! ds)))))

(comment
  (System/setProperty "MYSQL_HOST" "localhost")
  (main-)
  (do
    (def db {:dbtype "mysql" :host "workshop-db.wmatson.com"
             :user "root" :password (pass/get-password :orders-db/admin)
             :dbname "orders"})

    (def readonly-db
      {:dbtype "mysql" :host "workshop-db.wmatson.com"
       :user "read-only" :password (pass/get-password :orders-db/read-only)
       :dbname "orders"})

    (def ds (jdbc/get-datasource db)))

  (create-schema! ds)
  (create-read-user ds)
  (create-items ds)

  (let [possible-item-ids (map :items/id (sql/query ds ["SELECT id FROM items"]))
        article-ids [340 404 100 173]
        gen-args {:user-ids (range 1000)
                  :event-ids (apply concat (range 500) (repeat 50 article-ids))
                  :item-ids possible-item-ids}]
    (->> (repeatedly 300 #(gen-purchase gen-args))
         (populate-purchases! ds)))

  (def read-ds (jdbc/get-datasource readonly-db))

  (sql/query read-ds
             ["SELECT * FROM purchases p 
               JOIN items i ON p.itemId = i.id
               JOIN purchase_adjacent_events pae ON pae.purchaseId = p.id
               LIMIT 10"])

  (delete-schema! ds))

