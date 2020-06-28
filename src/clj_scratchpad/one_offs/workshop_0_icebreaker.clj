(ns clj-scratchpad.one-offs.workshop-0-icebreaker
  (:require [clj-http.client :as http]))

(def base-url "http://localhost:8084/api/")

(comment
  ;; Provide your name (or username/screen-handle) and favorite function
  ;; 
  ;; This exercise should confirm you have the required installation setup
  ;; 
  ;; If you're not following along in Clojure, feel free to use the swagger
  ;; page to participate in this exercise:
  ;; 
  ;; http://localhost:8084/api-docs/index.html#!/fave45func/post_api_favorite_function

  (http/request {:url (str base-url "favorite-function")
                 :method :post
                 :as :json
                 :form-params {:name "Your Name"
                               :function "Your favorite function"}}))
