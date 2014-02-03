(defproject clortex "0.1.1-SNAPSHOT"
  :description "Clortex: Implementation in Clojure of Jeff Hawkins' Hierarchical Temporal Memory & Cortical Learning Algorithm"
  :url "https://github.com/fergalbyrne/clortex"
  :license {:name "Apache Public License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :profiles {:dev {:dependencies [[midje "1.6.0"]]}}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [incanter/incanter-core "1.5.4"]
                 [incanter/incanter-io "1.5.4"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/data.json "0.2.1"]
                 [enlive "1.1.5"]
                 [clojure-opennlp "0.3.2"]]
  :documentation {:files {"doc/index"
                          {:input "test/clortex/core_test.clj"
                           :title "clortex"
                           :sub-title "Clojure Library for Jeff Hawkins' Hierarchical Temporal Memory"
                           :author "Fergal Byrne"
                           :email  "fergalbyrnedublin@gmail.com"
                           :tracking "UA-31320512-2"}
                          }}
  :eval-in-leiningen true)

