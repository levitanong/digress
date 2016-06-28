(ns telepathy.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close! chan]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def ws-url "ws://localhost:3000/async")
(enable-console-print!)

#_(go 
    (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:3000/async"))]
      (go-loop []
        (println "eh")
        (let [{:keys [message]} (<! ws-channel)]
          (cond
            error (println "error: " error)
            message (println "message: " message)
            :else (println "nothing"))
          (recur)))
      #_(>! ws-channel "tangina")
      #_(println "sending spam to server")
      #_(>! ws-channel "spam from client")))

(defn- send-msgs! [new-msg-ch server-ch]
  (go-loop []
    (when-let [msg (<! new-msg-ch)]
      (>! server-ch msg)
      (recur))))

(defn- receive-msgs! [server-ch]
  (go-loop []
    (let [{:keys [message]} (<! server-ch)]
      (when message
        (println message)
        (recur))))
  #_(go-loop []
      (let [{:keys [message error] :as msg-container} (<! server-ch)]
        (println msg-container)
        #_(cond
            error (println "error: " error)
            message (swap! msgs conj message)
            :else (println "eh?"))
        (when message
          (recur)))))

(defui WSTest
  Object
  (render [this]
    (dom/div nil
      "YO"
      (dom/button
          #js {:onClick (fn []
                          (let [send-msg-ch (get (om/props this) :send-msg-ch)]
                            (put! send-msg-ch "Testing, 1 2 3")))}
        "Send server a message"))))

(def wstest (om/factory WSTest))

(go
  (let [{:keys [ws-channel]} (<! (ws-ch ws-url))]
    (receive-msgs! ws-channel)
    (js/ReactDOM.render
     (wstest {:send-msg-ch (doto (chan)
                             (send-msgs! ws-channel))})
     (gdom/getElement "app"))))
