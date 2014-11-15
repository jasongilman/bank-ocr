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

(def account-number-parse-examples
  "TODO"
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


(deftest parse-account-number-test
  (testing "valid account numbers"
    (doseq [[entry account-num] account-number-parse-examples]
      (is (= account-num (b/parse-account-number entry)))))
  (testing "invalid account numbers"
    (is (= "49006771?"
           (b/parse-account-number
             (str "    _  _  _  _  _  _     _ \n"
                  "|_||_|| || ||_   |  |  | _ \n"
                  "  | _||_||_||_|  |  |  | _|\n"
                  "\n"))))
    (is (= "12345678?"
           (b/parse-account-number
             (str "    _  _     _  _  _  _  _ \n"
                  "  | _| _||_||_ |_   ||_||_|\n"
                  "  ||_  _|  | _||_|  ||_| _ \n"
                  "\n"))))
    (is (= "?????????"
           (b/parse-account-number
             (str "    _  _     _  _  _  _  _ \n"
                  "     | _| _| _ |_   ||_||_|\n"
                  "  ||_  _   | _|| |   | | _ \n"
                  "\n"))))))

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

(deftest parse-account-numbers-test
  (let [entry-file-string (account-nums->entry-file-string (vals account-number-parse-examples))]
    ;; Make sure we generate a valid file
    (is (= (str/join (keys account-number-parse-examples)) entry-file-string))
    (is (= (vals account-number-parse-examples) 
           (b/parse-account-numbers (io/reader (StringReader. entry-file-string)))))))

(def example-valid-account-numbers
  "Example valid account numbers"
  ["457508000"
   "711111111"
   "123456789"
   "490867715"])

(def example-invalid-account-numbers
  "Example invalid account numbers"
  ["664371495"
   "888888888"
   "490067715"
   "012345678"])

(deftest validate-account-numbers-test
  (doseq [account-num example-valid-account-numbers]
    (is (b/valid-account-number? account-num)))
  (doseq [account-num example-invalid-account-numbers]
    (is (not (b/valid-account-number? account-num)))))


