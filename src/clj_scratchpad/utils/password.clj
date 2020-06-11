(ns clj-scratchpad.utils.password
  (:import [javax.swing JPasswordField JOptionPane]))

(defn- get-password-by-dialog [key]
  (let [passwordField (JPasswordField.)]
    (JOptionPane/showConfirmDialog nil passwordField
                                   (str "Enter password for " key) 
                                   JOptionPane/OK_CANCEL_OPTION
                                   JOptionPane/PLAIN_MESSAGE)
    (String. (.getPassword passwordField))))

(defonce passwords (atom {}))

(defn get-password
  ([key] (get-password key false))
  ([key force-get]
   (let [current-password (get @passwords key)]
     (if (or force-get (nil? current-password))
       (-> passwords
           (swap! assoc key (get-password-by-dialog key))
           (get key))
       current-password))))

(comment
  (get-password :test)
  (get-password :test true))

