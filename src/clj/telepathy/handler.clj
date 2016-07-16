(ns telepathy.handler
  (:require
   [com.stuartsierra.component :as component]
   [telepathy.api :as api]
   [clojure.java.io :as io]
   [org.httpkit.server :refer [run-server]]
   [chord.http-kit :refer [wrap-websocket-handler]]
   [compojure.core :refer [routes GET POST ANY DELETE]]
   [compojure.route :refer [not-found] :as route]
   [compojure.handler :refer [site]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.util.response :refer [response content-type resource-response]]
   [clojure.core.async :refer [<! >! go go-loop timeout]]))

(def middleware (-> site-defaults
                    (assoc-in [:static :resources] "/")
                    (assoc-in [:security :anti-forgery] false)))

(defn socket-handler [{:keys [ws-channel] :as req}]
  (go
    #_(loop []
        (>! ws-channel "Message from server")
        (<! (timeout 1000))
        (recur))
    (loop []
      (let [{:keys [message]} (<! ws-channel)]
        (when message
          (println "Message received: " message)
          (>! ws-channel "Copy that.")
          (recur))))))


(defn app-routes [app-component]
  (-> (routes
       (GET "/" [] (-> (resource-response "index.html")
                       (content-type "text/html")))
       (GET "/ws" [] (-> socket-handler
                         (wrap-websocket-handler))))
      (site)
      (wrap-defaults middleware)))

(defrecord WebServer [port server app-component]
  component/Lifecycle
  (start [component]
    (let [handler (app-routes app-component)]
      (assoc component :server
             (run-server handler {:port port}))))
  (stop [component]
    (when server
      (server)
      component)))

(defn web-server [port]
  (map->WebServer {:port port}))
