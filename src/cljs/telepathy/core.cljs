(ns telepathy.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close! chan timeout] :as a]
            [clojure.string :as string])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:import [goog Uri]
           [goog.net Jsonp]))

(enable-console-print!)
(def ws-url "ws://localhost:3000/ws")

(defn- receive-msgs! [ws-channel]
  (go-loop []
    (let [{:keys [message error]} (<! ws-channel)]
      (when error
        (println error))
      (when message
        (println message)
        #_(<! (timeout 100))
        (recur)))))

(defn- send-msgs! [new-msg-ch ws-channel]
  (go
    (loop []
      (when-let [msg (<! new-msg-ch)]
        (println "sending message: " msg)
        (>! ws-channel msg)
        (recur)))))

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [state ast] :as env} key params]
  (println ast)
  (let [st @state]
    (if-let [value (get st key)]
      {:value value}
      {:value :not-found})))

(defui Home
  Object
  (render [this]
    (let [{:keys [send-msg-ch msg/list]} (om/props this)]
      (dom/div nil
        "hi"
        (dom/button #js {:onClick (fn [] (put! send-msg-ch "Testing, 123"))}
          "Send server a message")))))

(go
  (let [{:keys [ws-channel]} (<! (ws-ch ws-url))]
    (receive-msgs! ws-channel)
    (om/add-root! (om/reconciler
                   {:state (atom {:msg/list []
                                  :send-msg-ch (doto (chan)
                                                 (send-msgs! ws-channel))})
                    :parser (om/parser {:read read})})
                  Home
                  (gdom/getElement "app"))))
