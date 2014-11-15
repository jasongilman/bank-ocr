(ns bank-ocr.core
  "TODO"
  (:require [clojure.string :as str]
            [clojure.set :as set]))



;; Each entry is 4 lines long, and each line has 27 characters. The first 3 lines of each entry 
;; contain an account number written using pipes and underscores, and the fourth line is blank.

;; Each account number should have 9 digits, all of which should be in the range 0-9. A normal file 
;; contains around 500 entries.

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
  (set/map-invert char-matrix->digit))

(def example
  (str 
    "    _  _     _  _  _  _  _ \n" 
    "  | _| _||_||_ |_   ||_||_|\n"  
    "  ||_  _|  | _||_|  ||_| _|\n"
    "\n"
    "    _  _     _  _  _  _  _ \n" 
    "  | _| _||_||_ |_   ||_||_|\n"  
    "  ||_  _|  | _||_|  ||_| _|\n"))


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
       (map char-matrix->digit)
       ;; Join together the digits into an account number
       str/join))



