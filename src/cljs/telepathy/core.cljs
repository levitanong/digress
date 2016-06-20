(ns telepathy.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)
(println "yo")

(go
  (let [{:keys [ws-channel]} (<! (ws-ch "ws://localhost:3000/async"))
        #_{:keys [message error]} #_(<! ws-channel)]
    #_(if-not error
        (println "message: " message)
        (println "error: " error))
    #_(>! ws-channel "tangina")
    (println "sending spam to server")
    (put! ws-channel "spam from client")
    #_(js/setTimeout #(recur) 1000)))

(defui WSTest
  Object
  (render [this]
          (dom/div nil
                   "YO"
                   (dom/button
                    #js {:onClick (fn []
                                    (println "dude"))}
                    "Dude"))))

(def wstest (om/factory WSTest))

(js/ReactDOM.render (wstest) (gdom/getElement "app"))
