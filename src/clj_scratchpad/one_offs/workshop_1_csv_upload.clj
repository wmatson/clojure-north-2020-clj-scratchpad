(ns clj-scratchpad.one-offs.workshop-1-csv-upload
  (:require [clj-scratchpad.utils.csv :as csvu]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [medley.core :as medley]))

(comment
  ;; Upload location: http://workshop.wmatson.com:8080/

  (take 10 (csvu/read-maps "resources/exercise-1.csv")))
