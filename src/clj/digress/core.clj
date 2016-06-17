(ns digress.core
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server with-channel websocket? on-close on-receive send!]]
            [compojure.core :refer [routes GET POST ANY DELETE context defroutes]]
            [compojure.route :refer [not-found] :as route]
            [compojure.handler :refer [site]]
            [clojure.pprint :refer [pprint]]
            ))

#_(defn app-routes
    "Returns the web handler function as a closure over the
  application component."
    [app-component]
    (routes
     (GET "/" [] "hi")))

(defn async-handler [req]
  (with-channel req channel
    (pprint [req channel "wut"])
    (on-close channel (fn [status]
                        (println "channel closed")))
    (if (websocket? channel)
      (println "Websocket Channel")
      (println "HTTP Channel"))
    (on-receive channel (fn [data]
                          (send! channel data)))))

(defroutes app-routes
  (GET "/" [] "<h1>Hello World!!!</h1")
  (GET "/tae" [] "<h2>Tae naman o</h2")
  (GET "/async" [] async-handler))

(defonce server (atom nil))

(defn stop-server []
  (when @server
    (@server)
    (reset! server nil)))

(defn -main [& args]
  (reset! server (run-server (site #'app-routes) {:port 8080})))
