(ns clj-scratchpad.protos.user-data-service
  (:import [java.util Random])
  (:require [compojure.api.sweet :refer [api GET POST context]]
            [ring.util.http-response :refer [ok permanent-redirect]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :as jetty]))

(def first-names ["Bob" "Sally" "Alice" "John" "Jacob" "Jill" "Jack" "Barbara"])
(def last-names ["Cooper" "Smith" "Teller" "Liontamer" "Bane" "Jilstatter"])

(def levels ["Junior" "Apprentice" "Chief" "Senior" "Journeyman" "VP"])
(def occupations ["Wormhole Inspector" "Doctor" "Lawyer"
                  "Carpenter" "Dentist" "Javelin Catcher" "Athlete"
                  "Monkey" "Hot Dog Eater"])

(def colors ["Red" "Blue" "Green" "Yellow" "Purple" "Chartreuse" "Pink" "Teal" "Burgundy" "Black" "Gray" "Turquoise"])

(defn choose [random items]
  (let [n (.nextInt random (count items))]
    (nth items n)))

(defn get-user [id]
  (let [random (Random. id)]
    ;; For some reason in the context of `choose`, we always got Jill the first time until I added this
    (.nextInt random)
    {:id id
     :name (str (choose random first-names)
                " "
                (choose random last-names))
     :age (+ 13 (.nextInt random 50))
     :title (str (choose random levels)
                 " "
                 (choose random occupations))
     :favorite-color (if (#{27} id)
                       "#c80202"
                       (choose random colors))}))

(defonce func-fave-counts (atom {}))

(def handler
  (api {:swagger {:ui "/api-docs"
                  :spec "/swagger.json"
                  :data {:info {:title "User Data Service"
                                :description "HandBazaar's service for user-centric data"}
                         :consumes ["application/json"]
                         :produces ["application/json"]}}}
       (context "/" []
         :no-doc true
         (GET "/" []
           (permanent-redirect "/api-docs")))
       (context "/api" []
         (context "/favorite-function" []
           :tags ["fave-func"]
           (POST "/" []
             :form-params [function :- String,
                           name :- String]
             (let [new-count (-> func-fave-counts
                                 (swap! #(update % function (fnil inc 0)))
                                 (get function))]
               (ok {:received {:name name
                               :function function}
                    :new-fave-count new-count})))
           (GET "/" []
             (ok {:fave-counts @func-fave-counts})))
         (GET "/users" []
           :tags ["users"]
           :query-params [{limit :- Long 10}
                          {skip :- Long 0}]
           (let [start skip
                 num-items (min limit 100)]
             (ok {:result (->> (min (+ start num-items) 1000)
                               (range start)
                               (map get-user))}))))))

(def app
  (wrap-reload #'handler))

(defn -main []
  (jetty/run-jetty app {:port 8084}))

(comment
  (def server (jetty/run-jetty app {:join? false :port 8084}))
  (.stop server)

  (->> (range 10)
       (map #(Random. %))
       (map #(.nextInt % 7))
       (map #(nth first-names %)))

  (->> (range 10)
       (map #(Random. %))
       (map #(choose % first-names))))

