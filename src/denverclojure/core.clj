(ns denverclojure.core
  (:use [noir.core]
        [hiccup.core]
        [hiccup.page]
        [hiccup.element])
  (:require [noir.server :as server]))
(defpage "/user/:id" {:keys [id]}
  (str "Hello " id))

(defpage "/path/:var" {:keys [var]}
  var)

(defpage [:post "/input"] {:keys [user pass]}
  (str "User: " user ", Password: " pass))

(defpage "/html" []
  (html [:div#foo.bar "Hello World"]))

(defpartial user [n]
  [:p [:h2 (str "Hello " n)]])

(defpartial users [names]
  (for [name names]
    (user name)))


(def members [{:name "Lee"
               :github "http://github.com/dakrone"}
              {:name "Daniel"
               :github "http://aoeu.com"}
              {:name "J......"
               :github "http://kitties.com"}
              {:name "Bart"
               :github "http://github.com/kasterma"}])

(defpartial make-user [member]
  [:li (str (:name member) " - ") (link-to (:github member) "github")])

(defpartial valid-html []
  [:html [:head [:title "Denver Clojure"]]])

(defpage "/" []
  (html5
   [:center [:h1 "Welcome to Denver Clojure"]]
   [:ul (map make-user members)]))

(defn -main [& [port]]
  (server/start (Integer. (or port "8080"))))

