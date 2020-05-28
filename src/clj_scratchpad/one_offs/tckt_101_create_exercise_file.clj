(ns clj-scratchpad.one-offs.tckt-101-create-exercise-file
  (:require [medley.core :as medley]
            [clj-scratchpad.utils.csv :as csvu]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(defn- generate-item
  ([] (generate-item (rand-int 100000)))
  ([id]
   (reduce (fn gen-extra-col [acc extra-col-name]
             (assoc acc extra-col-name (inc (rand-int 100))))
           {"event-id" id
            "event-name" (str (rand-nth ["Bashful" "Amiable" "Comical" "Zoological" "Criminal"
                                         "Sponsored" "Terrific" "Clickbait-y" "Cute" "Hilarious"])
                              " "
                              (rand-nth ["Wednesday" "Clown Story" "Clickbait" "Monkey Attack"
                                         "Food Review" "Physics Discovery"]))
            "event-type" (rand-nth ["Podcast" "Article" "Video"])}
           ["extra-1" "extra-2" "extra-3"])))

(defn- gen-with-duplicates [item-count]
  (let [half (int (/ item-count 2))
        ids (shuffle (range item-count))]
    (->> (map generate-item ids)
         (partition-all half (dec half))
         (apply concat))))

(comment
  (with-open [writer (io/writer "resources/exercise-1.csv")]
    (->> (gen-with-duplicates 500)
         (csvu/maps->csv-data)
         (csv/write-csv writer))))