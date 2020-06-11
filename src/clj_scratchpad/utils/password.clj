(ns clj-scratchpad.utils.password
  (:import [javax.swing JPasswordField JOptionPane]))

(defn- get-password-by-dialog []
  (let [passwordField (JPasswordField.)]
    (JOptionPane/showConfirmDialog nil passwordField
                                   "Enter Password" JOptionPane/OK_CANCEL_OPTION
                                   JOptionPane/PLAIN_MESSAGE)
    (String. (.getPassword passwordField))))

(defonce passwords (atom {}))

(defn get-password
  ([key] (get-password key false))
  ([key force-get]
   (let [current-password (get @passwords key)]
     (if (or force-get (nil? current-password))
       (-> passwords
           (swap! assoc key (get-password-by-dialog))
           (get key))
       current-password))))

(comment
  (get-password :test))

