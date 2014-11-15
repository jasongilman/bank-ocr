(defproject bank-ocr "0.1.0-SNAPSHOT"
  :description "A command line application written in Clojure that can parse account number entries that are \"printed\" using a combination of spaces, pipes, and underscores."
  :url "https://github.com/jasongilman/bank-ocr"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/test.check "0.5.9"]]
  
  :profiles 
  {:dev {:source-paths ["dev" "src" "test"]
         :dependencies [[org.clojure/tools.namespace "0.2.7"]
                        [org.clojars.gjahad/debug-repl "0.3.3"]]}
   :uberjar {:main bank-ocr.AccountReader
             :aot :all}})
