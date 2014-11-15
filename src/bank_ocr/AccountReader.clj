(ns bank-ocr.AccountReader
  "Class containing the main method for the Bank OCR application. The main method accepts a single 
  argument of a file containing account number entries to parse. It reads the account numbers
  and prints them to standard out. Account numbers that can not be parsed will be printed with a ?
  mark for the characters that can not be parsed and followed by ILL. Account numbers that are 
  invalid will be followed by ERR."
  (:require [bank-ocr.core :as b])
  (:gen-class :main true))

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
  "Prints the account number to standard out along with a 3 character status string"
  [account-num]
  (let [status (account-number->status account-num)]
    (println account-num status)))

(defn -main
  [& args]
  (validate-args args)
  (let [[file-path] args]
    (try 
      (b/process-account-numbers-file file-path print-account-number-with-status)
      (catch java.io.FileNotFoundException e
        (println "File" file-path "does not exist.")
        (System/exit 1))
      (catch Throwable e
        (println "Unexpected error:" (.getMessage e))
        (.printStackTrace e)
        (System/exit 1)))))