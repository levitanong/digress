(ns telepathy.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)
(println "yo")

(defui WSTest
  Object
  (render [this]
          (dom/div nil "YO")))

(def wstest (om/factory WSTest))

(js/ReactDOM.render (wstest) (gdom/getElement "app"))
