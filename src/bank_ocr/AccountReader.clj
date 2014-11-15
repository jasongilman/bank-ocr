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

(defn -main
  [& args]
  (validate-args args)
  (b/process-account-numbers-file (first args) println))