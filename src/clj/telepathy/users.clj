(ns telepathy.users
  (:require [datomic.api :as d]))

(defn add [conn email password]
  (d/transact conn [{:db/id (d/tempid :db.part/user)
                     :user/email email
                     :user/password password}]))

(defn all [db]
  (d/q '[:find ?e ?p
         :where
         [?eid :user/email ?e]
         [?eid :user/password ?p]]
       db))
