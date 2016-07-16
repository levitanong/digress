(set-env!
 :source-paths   #{"src/cljs" "src/clj"}
 :resource-paths #{"resources"}
 :dependencies '[[adzerk/boot-cljs "1.7.228-1" :scope "test"]
                 [adzerk/boot-reload "0.4.11" :scope "test"]

                 [adzerk/boot-cljs-repl   "0.3.0" :scope "test"]
                 [com.cemerick/piggieback "0.2.1"  :scope "test"]
                 [weasel                  "0.7.0"  :scope "test"]

                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.89"]
                 [org.clojure/core.async "0.2.385"]

                 [environ "1.0.3"]
                 [boot-environ "1.0.3"]

                 [org.danielsz/system "0.3.0-SNAPSHOT"]
                 [org.clojure/tools.nrepl "0.2.12"]

                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.5.0"]
                 [xsc/pem-reader "0.1.1"] ;; specifically for sending stuff to apple servers.
                 [jarohen/chord "0.7.0"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [com.datomic/datomic-free   "0.9.5359"
                  #_:exclusions #_[joda-time org.slf4j/slf4j-nop org.slf4j/slf4j-log4j12]]
                 ;; client
                 [org.omcljs/om "1.0.0-alpha37"]])

(require
 '[clojure.pprint :refer [pprint]]
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[telepathy.systems :refer [dev-system prod-system]]
 '[environ.boot :refer [environ]]
 '[system.boot :refer [system run]]
 '[system.repl :as sysrepl])

(defn conn []
  (get-in sysrepl/system [:db :conn]))

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port "3000"
                  :db-uri "datomic:mem://telepathy"})
   (watch :verbose true)
   (speak)
   (system :sys #'dev-system :auto true :files ["handler.clj"])
   (reload)
   (cljs :source-map true)
   (repl :server true)))

(deftask dev-cljs-repl
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port "3000"
                  :db-uri "datomic:mem://telepathy"})
   (watch :verbose true)
   (system :sys #'dev-system :auto true :files ["handler.clj"])
   (reload)
   (cljs-repl)
   (cljs :source-map true)))

(deftask dev-run
  "Run a dev system from the command line"
  []
  (comp
   (environ :env {:http-port "3000"})
   (cljs)
   (run :main-namespace "telepathy.core" :arguments [#'dev-system])
   (wait)))

(deftask prod-run
  "Run a prod system from the command line"
  []
  (comp
   (environ :env {:http-port "8008"
                  :repl-port "8009"})
   (cljs :optimizations :advanced)
   (run :main-namespace "telepathy.core" :arguments [#'prod-system])
   (wait)))
