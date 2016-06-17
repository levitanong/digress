(defproject digress "0.1.0-SNAPSHOT"
  :description "A chat app that allows multiple conversations with the same contact."
  :min-lein-version "2.6.1"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.36"]
                 [org.omcljs/om "1.0.0-alpha34"]
                 [com.stuartsierra/component "0.3.1"]
                 [http-kit "2.2.0-alpha2"]
                 [compojure "1.5.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [figwheel-sidecar "0.5.0-SNAPSHOT" :scope "test"]]
  :plugins [[cider/cider-nrepl "0.13.0-SNAPSHOT"]]
  :source-paths ["src/clj"]
  :main digress.core)
