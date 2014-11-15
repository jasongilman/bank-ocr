(ns bank-ocr.test.core
  (:require [clojure.test :refer :all]
            [clojure.test.check.properties :refer [for-all]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [bank-ocr.core :as b])
  (:import java.io.StringReader))

(def digits
  "Generator for digits of an account number"
  (gen/elements "012345689"))

(def account-numbers
  "Generator for account number. Each account number is a string of 9 digits"
  (gen/fmap str/join (gen/vector digits 9)))

(defspec parse-account-number-spec 1000
  (for-all [account-num account-numbers]
    (= account-num (b/parse-account-number (b/print-account-number account-num)))))

(def user-story-1-examples
  {(str " _  _  _  _  _  _  _  _  _ \n"
        "| || || || || || || || || |\n"
        "|_||_||_||_||_||_||_||_||_|\n"
        "\n")
   "000000000"
   (str "                           \n"
        "  |  |  |  |  |  |  |  |  |\n"
        "  |  |  |  |  |  |  |  |  |\n"
        "\n")
   "111111111"
   (str " _  _  _  _  _  _  _  _  _ \n"
        " _| _| _| _| _| _| _| _| _|\n"
        "|_ |_ |_ |_ |_ |_ |_ |_ |_ \n"
        "\n")
   "222222222"
   (str " _  _  _  _  _  _  _  _  _ \n"
        " _| _| _| _| _| _| _| _| _|\n"
        " _| _| _| _| _| _| _| _| _|\n"
        "\n")
   "333333333"
   (str "                           \n"
        "|_||_||_||_||_||_||_||_||_|\n"
        "  |  |  |  |  |  |  |  |  |\n"
        "\n")
   "444444444"
   (str " _  _  _  _  _  _  _  _  _ \n"
        "|_ |_ |_ |_ |_ |_ |_ |_ |_ \n"
        " _| _| _| _| _| _| _| _| _|\n"
        "\n")
   "555555555"
   (str " _  _  _  _  _  _  _  _  _ \n"
        "|_ |_ |_ |_ |_ |_ |_ |_ |_ \n"
        "|_||_||_||_||_||_||_||_||_|\n"
        "\n")
   "666666666"
   (str " _  _  _  _  _  _  _  _  _ \n"
        "  |  |  |  |  |  |  |  |  |\n"
        "  |  |  |  |  |  |  |  |  |\n"
        "\n")
   "777777777"
   (str " _  _  _  _  _  _  _  _  _ \n"
        "|_||_||_||_||_||_||_||_||_|\n"
        "|_||_||_||_||_||_||_||_||_|\n"
        "\n")
   "888888888"
   (str " _  _  _  _  _  _  _  _  _ \n"
        "|_||_||_||_||_||_||_||_||_|\n"
        " _| _| _| _| _| _| _| _| _|\n"
        "\n")
   "999999999"
   (str "    _  _     _  _  _  _  _ \n"
        "  | _| _||_||_ |_   ||_||_|\n"
        "  ||_  _|  | _||_|  ||_| _|\n"
        "\n")
   "123456789"})


(deftest parse-user-story-1-examples-test
  (doseq [[entry account-num] user-story-1-examples]
    (is (= account-num (b/parse-account-number entry)))))

(defn account-nums->entry-file-string
  [account-nums]
  (str (->> account-nums
         (map b/print-account-number)
         (str/join "\n"))
       "\n"))

(defspec parse-account-numbers-spec 100
  (for-all [account-nums (gen/vector account-numbers)]
    (let [account-entry-string (account-nums->entry-file-string account-nums)]
      (= account-nums 
         (b/parse-account-numbers (io/reader (StringReader. account-entry-string)))))))

(deftest parse-user-story-1-examples-file
  (let [entry-file-string (account-nums->entry-file-string (vals user-story-1-examples))]
    ;; Make sure we generate a valid file
    (is (= (str/join (keys user-story-1-examples)) entry-file-string))
    (is (= (vals user-story-1-examples) 
           (b/parse-account-numbers (io/reader (StringReader. entry-file-string)))))))

(def valid-account-numbers
  "Example valid account numbers"
  ["457508000"
   "711111111"
   "123456789"
   "490867715"])

(def invalid-account-numbers
  "Example invalid account numbers"
  ["664371495"
   "888888888"
   "490067715"
   "012345678"])

(deftest validate-account-numbers-test
  (doseq [account-num valid-account-numbers]
    (is (b/valid-account-number? account-num)))
  (doseq [account-num invalid-account-numbers]
    (is (not (b/valid-account-number? account-num)))))


