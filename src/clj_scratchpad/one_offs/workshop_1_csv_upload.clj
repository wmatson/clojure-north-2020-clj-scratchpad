(ns clj-scratchpad.one-offs.workshop-1-csv-upload
  (:require [clj-scratchpad.utils.csv :as csv]
            [medley.core :as medley]))

(comment
  ;; Upload location: http://workshop.wmatson.com:8080/

  (take 10 (csv/read-maps "resources/exercise-1.csv")))
