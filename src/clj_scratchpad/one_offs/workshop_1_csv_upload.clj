(ns clj-scratchpad.one-offs.workshop-1-csv-upload
  (:require [clj-scratchpad.utils.csv :as csvu]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [medley.core :as medley]))

(defn- keywordify-keys [event]
  (medley/map-keys keyword event))

(comment
  ;; Upload location: http://workshop.wmatson.com:8080/

  (->> (csvu/read-maps "resources/exercise-1.csv")
       (map keywordify-keys)
       (group-by :event-id)
       (medley/filter-vals #(> (count %) 1))
       (take 10))
  
  (with-open [writer (io/writer "resources/exercise-1-corrected.csv")]
    (->> (csvu/read-maps "resources/exercise-1.csv")
         set
         (csvu/maps->csv-data ["event-id" "event-name" "event-type" "extra-1"])
         (csv/write-csv writer))))
