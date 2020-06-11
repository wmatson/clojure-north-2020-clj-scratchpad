(ns clj-scratchpad.one-offs.tckt-101-populate-orders-db
  (:require [next.jdbc :as jdbc]
            [clj-scratchpad.utils.password :as pass]))

(def db {:dbtype "mysql" :host "52.27.179.12"
         :user "root" :password (pass/get-password :orders-db)
         :dbname "orders"})

(comment
  (def ds (next.jdbc/get-datasource db))

  (jdbc/execute! ds ["SELECT * FROM orders"]))

