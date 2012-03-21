(ns denverclojure.core
  (:use [noir.core]
        [hiccup.core]
        [hiccup.page]
        [hiccup.form]
        [hiccup.element]
        [cheshire.core])
  (:require [noir.server :as server]))

(defpage "/user/:id" {:keys [id]}
  (str "Hello " id))

(defpage "/path/:var" {:keys [var]}
  var)

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

(defpage "/users.json" []
  (encode members))

(defpage [:post "/input"] {:keys [user pass]}
  (html5
   (if (= pass "clojure")
     [:h2 "Logged in!"]
     [:h2 "Nope."])))

(defpage "/overtone" []
  html
  [:h2 "links"]
  [:ul]
  [:li "whatever" "http://overtone-video.org"]
)

(defpage "/form" []
  (html5
   (form-to [:post "/input"]
            [:p "Username:" (text-field "user")]
            [:p "Password:" (password-field "pass")]
            (submit-button "submit"))))

(defn -main [& [port]]
  (server/start (Integer. (or port "8080"))))

