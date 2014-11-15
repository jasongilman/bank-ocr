(ns bank-ocr.core
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
  {one "1"
   two "2"
   three "3"
   four "4"
   five "5"
   six "6"
   seven "7"
   eight "8"
   nine "9"
   zero "0"})

(def digit->char-matrix
  (set/invert-map char-matrix->digit))

(def example
  (str 
    "    _  _     _  _  _  _  _ \n" 
    "  | _| _||_||_ |_   ||_||_|\n"  
    "  ||_  _|  | _||_|  ||_| _|\n"
    "                           \n"   ))

(->> (str/split example #"\n")
     ;; Remove blank line
     drop-last
     (map (partial partition 3))
     transpose
     (map char-matrix->digit))

