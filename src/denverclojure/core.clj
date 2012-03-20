(ns denverclojure.core
  (:use [noir.core])
  (:require [noir.server :as server]))

(defpage "/" []
  "Hello World")

(defn -main [& [port]]
  (server/start (Integer. (or port "8080"))))
