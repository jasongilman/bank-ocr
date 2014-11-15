(ns bank-ocr.AccountReader
  "TODO"
  (:require [bank-ocr.core :as b])
  (:gen-class :main true))


;; TODO test file non existance

(defn- validate-args
  "Validates the arguments. Prints an error message and exits with a non-zero
  status if arguments are invalid."
  [args]
  (when (not= (count args) 1) 
    (println "Expects 1 argument of a file containing account entries to parse.")
    (System/exit 1)))

(defn account-number->status
  "Examines the account number to make sure it was parsed correctly and is valid. Returns a three
  character code indicating the problem or empty string if it is valid." 
  [account-num]
  (cond
    (re-find #"\?" account-num) "ILL"
    (not (b/valid-account-number? account-num)) "ERR"
    :else ""))

(defn- print-account-number-with-status
  "TODO"
  [account-num]
  (let [status (account-number->status account-num)]
    (println account-num status)))

(defn -main
  [& args]
  (validate-args args)
  (b/process-account-numbers-file (first args) print-account-number-with-status))