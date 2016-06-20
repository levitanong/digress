(require
 '[figwheel-sidecar.repl-api :as ra]
 '[com.stuartsierra.component :as component]
 '[org.httpkit.server :refer [run-server]]
 '[system.components.http-kit :refer [new-web-server]]
 '[telepathy.core :as telepathy]
 #_'[ring.component.jetty :refer [jetty-server]])

(def figwheel-config
  {:figwheel-options {} ;; <-- figwheel server config goes here 
   :build-ids ["dev"]   ;; <-- a vector of build ids to start autobuilding
   :all-builds          ;; <-- supply your build configs here
   [{:id "dev"
     :figwheel true
     :source-paths ["src/cljs"]
     :compiler {:main "telepathy.core"
                :asset-path "js"
                :output-to "resources/public/js/main.js"
                :output-dir "resources/public/js/"
                :verbose true}}]})

(defrecord Figwheel []
  component/Lifecycle
  (start [config]
    (ra/start-figwheel! config)
    config)
  (stop [config]
    ;; you may want to restart other components but not Figwheel
    ;; consider commenting out this next line if that is the case
    (ra/stop-figwheel!)
    config))

(defn handler [request]
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :body    "Hello World"})

(def system
  (atom
   (component/system-map
    :app-server (new-web-server 3000 telepathy/app-routes #_handler)
    :figwheel   (map->Figwheel figwheel-config))))

(defn start []
  (swap! system component/start))

(defn stop []
  (swap! system component/stop))

(defn reload []
  (stop)
  (start))

(defn repl []
  (ra/cljs-repl))
