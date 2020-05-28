(ns clj-scratchpad.utils.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn maps->csv-data 
  ([maps] (maps->csv-data (keys (first maps)) maps))
  ([headers maps]
   (->> maps
        (map #(map %2 %1) (repeat headers))
        (cons headers))))

(defn csv-data->maps [[headers & data]]
  (map zipmap (repeat headers) data))

(defn read-maps [filename]
  (with-open [reader (io/reader filename)]
    (doall (csv-data->maps (csv/read-csv reader)))))

(comment
  (read-maps "resources/example.csv")
  
  (->> (read-maps "resources/example.csv")
       (maps->csv-data ["a" "c"])))