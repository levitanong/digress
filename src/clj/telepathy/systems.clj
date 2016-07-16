(ns telepathy.systems
  (:require
   [com.stuartsierra.component :as component]
   [system.repl :as sysrepl :refer [system set-init! start stop reset]]
   (system.components
    #_[http-kit :refer [new-web-server]]
    [repl-server :refer [new-repl-server]])
   [telepathy.handler :refer [web-server]]
   [telepathy.api :refer [new-datomic new-telepathy]]
   [environ.core :refer [env]]))

(defn dev-system []
  (component/system-map
   :db (new-datomic (env :db-uri))
   :server (component/using
            (web-server (Integer. (env :http-port)))
            {:app :app})
   :app (component/using
         (new-telepathy)
         {:db :db})))

(defn prod-system []
  )
