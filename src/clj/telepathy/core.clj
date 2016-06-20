(ns telepathy.core
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server]]
            #_[system.components.http-kit :refer [new-web-server]]
            [compojure.core :refer [routes GET POST ANY DELETE context defroutes]]
            [compojure.route :refer [not-found] :as route]
            [compojure.handler :refer [site]]
            [clojure.pprint :refer [pprint]]
            [chord.http-kit :refer [with-channel wrap-websocket-handler]]
            [clojure.core.async :refer [<! >! put! close! go]]))

#_(defn app-routes
    "Returns the web handler function as a closure over the
  application component."
    [app-component]
    (routes
     (GET "/" [] "hi")))

(defn async-handler [{:keys [ws-channel] :as req}]
  (go
    (loop []
      (let [{:keys [message]} (<! ws-channel)]
        (when message
          (println "Received message from client: " message)
          (recur))
        #_(close! ws-channel)))))

(def app-routes
  (site
   (routes
    (GET "/" [] "<h1>Hello World!!!</h1")
    (GET "/tae" [] "<h2>Tae naman o</h2")
    (GET "/async" req #_async-handler ((wrap-websocket-handler async-handler) req) #_(-> async-handler
                                                                                         wrap-websocket-handler)))))

(defonce server (atom nil))

(defn stop-server []
  (when @server
    (@server)
    (reset! server nil)))

(defn -main [& args]
  (reset! server (run-server #'app-routes
                             {:port (if-let [port (System/getenv "PORT")]
                                      (Integer. port)
                                      8080)})))

(defn restart-server []
  (stop-server)
  (-main))
