# Bank OCR

A command line application written in Clojure that can parse account number entries that are "printed" using a combination of spaces, pipes, and underscores. 

An example account number which represents "123456789":

```
    _  _     _  _  _  _  _
  | _| _||_||_ |_   ||_||_|
  ||_  _|  | _||_|  ||_| _| 

```

Each entry is 4 lines long, and each line has 27 characters. The first 3 lines of each entry contain an account number written using pipes and underscores, and the fourth line is blank. An account number consists of 9 digits.

## Running Tests

    lein test

## Building

    lein uberjar

## Usage

Execute `java -jar target/bank-ocr-0.1.0-SNAPSHOT-standalone.jar` with a single argument of the entry file to parse.

Each parsed account number is printed out. If a digit can not be parsed `?` will be used. If an account number can not be parsed it will be followed by "ILL". If an account number is not valid it will be followed by "ERR".

There are some sample files in sample_files.zip.

Example:

    java -jar target/bank-ocr-0.1.0-SNAPSHOT-standalone.jar sample_files/mixed_account_entries.txt 

=>

```
000000051 
49006771? ILL
1234?678? ILL
000000000 
111111111 ERR
```



## License

Copyright © 2014 Jason Gilman

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
