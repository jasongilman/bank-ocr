(ns bank-ocr.core
  "Main namespace for the Bank OCR application. It contains functions for parsing and validating 
  account numbers"
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]))

(defn transpose 
  "Matrix transposition in Clojure. Takes a sequence of sequences and groups together the elements
  of the subsequences with the same index.
  
  (transpose [[1 2 3]
              [4 5 6]
              [7 8 9]])
  => [[1 4 7]
      [2 5 8]
      [3 6 9]]
  
  Source: http://stackoverflow.com/questions/10347315/matrix-transposition-in-clojure"
  [m]
  (apply mapv vector m))

;; Vars containing matrices of the characters of a printed digit. The rows of each matrix represent
;; the separate lines of the printed digit. 

(def one 
  [[\space \space \space]
   [\space \space \|]
   [\space \space \|]])

(def two
  [[\space \_ \space]
   [\space \_ \|]
   [\| \_ \space]])

(def three
  [[\space \_ \space]
   [\space \_ \|]
   [\space \_ \|]])

(def four
  [[\space \space \space]
   [\| \_ \|]
   [\space \space \|]])

(def five
  [[\space \_ \space]
   [\| \_ \space]
   [\space \_ \|]])

(def six
  [[\space \_ \space]
   [\| \_ \space]
   [\| \_ \|]])

(def seven
  [[\space \_ \space]
   [\space \space \|]
   [\space \space \|]])

(def eight
  [[\space \_ \space]
   [\| \_ \|]
   [\| \_ \|]])

(def nine
  [[\space \_ \space]
   [\| \_ \|]
   [\space \_ \|]])

(def zero
  [[\space \_ \space]
   [\| \space \|]
   [\| \_ \|]])

(def char-matrix->digit
  "A map of character matrices to their digits"
  {one \1
   two \2
   three \3
   four \4
   five \5
   six \6
   seven \7
   eight \8
   nine \9
   zero \0})

(def digit->char-matrix
  "A map of digits to character matrices."
  (set/map-invert char-matrix->digit))

(defn account-number-checksum
  "Returns the checksum of an account number. This can be calculated as follows:
  
  account number:  3  4  5  8  8  2  8  6  5
  position names:  d9 d8 d7 d6 d5 d4 d3 d2 d1
  
  checksum calculation:
  
  ((1*d1) + (2*d2) + (3*d3) + ... + (9*d9))"
  [account-num]
  (let [nums (map #(Long. ^String (str %)) account-num)]
    (apply + (map-indexed #(* (inc %1) %2) (reverse nums)))))

(defn valid-account-number?
  "Validates the account number by testing if the checksum of the account mod 11 is equal to 0. 
  Returns true if it's valid."
  [account-num]
  (zero? (mod (account-number-checksum account-num) 11)))

(defn print-account-number
  "Takes an account number string and 'prints' it to a string"
  [account-num]
  (let [entry (->> account-num
                   ;; Convert each digit of the account number into the character matrix
                   (map digit->char-matrix)
                   ;; Transpose the sequence of matrices to make it easier to connect together each line of the
                   ;; printed account number
                   transpose
                   ;; Connect together each line of the account number
                   (map (partial apply concat))
                   (map str/join)
                   ;; Join together the lines of the entry
                   (str/join "\n"))]
    ;; Append a blank line at the end of an entry
    (str entry "\n")))

(defn parse-account-number
  "Takes a printed account number entry representing digits as spaces, underscores, and pipes parses
  it and returns the account number."
  [entry]
  ;; TODO this would be a good place to use transducers to avoid multiple lazy sequences
  (->> entry
       str/split-lines
       ;; Remove blank line at the end. 
       (take 3)
       ;; Split each line into groups of 3 characters
       (map (partial partition 3))
       ;; Transpose the sequences of sequences to create a sequence 3 by 3 matrices. 
       ;; Each 3x3 character matrix represents a single account number digit
       transpose
       ;; Convert each character matrix into a digit
       (map #(get char-matrix->digit % \?))
       ;; Join together the digits into an account number
       str/join))

(defn parse-account-numbers
  "Parses account numbers from a reader returning entry numbers. Returns a lazy sequence of the 
  parsed account numbers"
  [r]
  ;; Read from reader one line at a time
  (->> (line-seq r)
       ;; Combine each set of 4 lines to form an entry string
       (partition 4)
       (map (partial str/join "\n"))
       ;; Parse each entry into an account number.
       (map parse-account-number)))

(defn process-account-numbers-file
  "Processes every account number in a file by calling the passed in function f. Processes the file
  line by line so it should be able to process very large files that would not fit in memory."
  [file-path f]
  (with-open [r (io/reader (io/file file-path))]
    (dorun (map f (parse-account-numbers r)))))


