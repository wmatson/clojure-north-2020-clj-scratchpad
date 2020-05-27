(ns clj-scratchpad.utils.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn csv-data->maps [[headers & data]]
  (map zipmap (repeat headers) data))

(defn read-maps [filename]
  (with-open [reader (io/reader filename)]
    (doall (csv-data->maps (csv/read-csv reader)))))

(comment
  (read-maps "resources/example.csv"))