(ns telepathy.api
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [routes GET POST ANY DELETE context defroutes]]
            [compojure.route :refer [not-found] :as route]
            [compojure.handler :refer [site]]
            [datomic.api :as d]
            [clojure.pprint :refer [pprint]]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [telepathy.users :as users]))

(defn- make-attr
  ([ident valueType]
   (make-attr ident valueType {}))
  ([ident valueType more]
   (into {:db.install/_attribute :db.part/db
          :db/id (d/tempid :db.part/db)
          :db/ident ident
          :db/valueType valueType
          :db/cardinality (or (:db/cardinality more)
                              :db.cardinality/one)}
         more)))

(def schema
  (map
   (partial apply make-attr)
   [[:user/email :db.type/string {:db/unique :db.unique/identity}]
    [:user/password :db.type/string]]))

(defrecord Datomic [uri conn schema]
  component/Lifecycle
  (start [component]
    (println "Starting Datomic")
    (let [db (d/create-database uri)
          conn (d/connect uri)] 
      (println "Transacting Schema")
      (d/transact conn schema)
      (assoc component :conn conn)))
  (stop [component] 
    (println "Stopping Datomic")
    (when conn (d/release conn))
    (assoc component :conn nil)))

(defn new-datomic [uri]
  (map->Datomic {:uri uri
                 :schema schema}))

(defrecord Telepathy [db users]
  component/Lifecycle
  (start [component]
    (let [conn (:conn db)]
      (users/add conn "levi.ong@gmail.com" "pass")
      (assoc component :users (users/all (d/db conn)))))
  (stop [component]
    component))

(defn new-telepathy []
  (map->Telepathy {}))

