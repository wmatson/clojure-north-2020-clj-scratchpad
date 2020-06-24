(ns clj-scratchpad.one-offs.workshop-1-csv-upload
  (:require [clj-scratchpad.utils.csv :as csvu]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [medley.core :as medley]))

(comment
  ;; It's 2 AM and a critical periodic data pull has failed, 
  ;; an analyst tried uploading the relevant file manually, 
  ;; but the upload form threw an exception.
  ;; 
  ;; The customer support line for the data source doesn't open
  ;; until 10AM local time, which is too late. It's up to you
  ;; to munge the data into a shape that works.
  ;; 
  ;; --------------------------------------------------------------------------
  ;; 
  ;; The last file that uploaded successfully is under 
  ;; <project-root>/resources/working-exercise-1.csv
  ;; 
  ;; Upload location: http://workshop.wmatson.com:8080/
  ;; File to upload <project-root>/resources/exercise-1.csv
  
  (take 10 (csvu/read-maps "resources/exercise-1.csv")))
