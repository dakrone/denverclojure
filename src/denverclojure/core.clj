(ns denverclojure.core
  (:require [clojure.java.io :refer [resource]]
            [noir.core :refer :all]
            [noir.options :as options]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [hiccup.form :refer :all]
            [hiccup.element :refer :all]
            [cheshire.core :refer :all]
            [noir.server :as server]
            [clj-http.client :as http]
            [clojure.tools.logging :refer [warn]]))

(defn get-api-key []
  (options/get :api-key))

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

(defn format-event [event]
  (select-keys event [:group :time :name :event_url]))

(defn get-events
  "Gets the events from Meetup.com"
  []
  (let [results (get-in (http/get "http://api.meetup.com/2/events"
                                  {:query-params {:group_id "1674002"
                                                  :key (get-api-key)}
                                   :as :json})
                        [:body :results])]
    (map format-event results)))

(defpartial event [e]
  [:li [:h3 (str (-> e :group :name))] (:name e)])

(defpage "/events" []
  (if (get-api-key)
    (html "Events:"
          [:ul (map event (get-events))])))

(def members [{:name "Lee"
               :github "http://github.com/dakrone"}
              {:name "Daniel Glauser"
               :github "https://github.com/danielglauser"}
              {:name "J......"
               :github "http://kitties.com"}
              {:name "Gregory"
               :github "http://github.com/gregoryg"}
              {:name "Bart"
               :github "http://github.com/kasterma"}
              {:name "Kurt Harriger"
               :github "http://github.com/kurtharriger"}])

(def links [{:url "http://overtone.github.com/"
             :desc "A music creation using Clojure" :link-text "Overtone"}
            {:url "http://webnoir.org/"
             :desc "Noir, a web framework"}
            {:url "https://github.com/cgrand/enlive"
             :desc "A templating system for Clojure with CSS-like selectors"
             :link-text "Enlive"}
            {:url "http://fullcontact.com"
             :desc "Turns partial contacts into full contacts!"
             :link-text "FullContact"}])

(defpartial make-user [member]
  [:li (str (:name member) " - ") (link-to (:github member) "github")])

(defpartial make-link [link]
  [:li (link-to (:url link)
                (if (:link-text link) (:link-text link) (:desc link)))
   (when (:link-text link) (str " - " (:desc link)))])

(defpartial valid-html []
  [:html [:head [:title "Denver Clojure"]]])

(defpage "/" []
  (html5
   (include-css "/css/denverclojure.css")
   [:h1 "Welcome to Denver Clojure"]
   [:h2 "Members of Denver Clojure"]
   [:ul#members (map make-user members)]
   [:h2 "Interesting links"]
   [:ul#links (map make-link links)]
   ))

(defpage "/users.json" []
  (encode members))

(defpage [:post "/input"] {:keys [user pass]}
  (html5
   (if (= pass "clojure")
     [:h2 "Logged in!"]
     [:h2 "Nope."])))

(defpage "/form" []
  (html5
   (form-to [:post "/input"]
            [:p "Username:" (text-field "user")]
            [:p "Password:" (password-field "pass")]
            (submit-button "submit"))))

(defn -main [& [port]]
  (let [key-file (resource "key")
        api-key (when key-file (.trim (slurp key-file)))]
    (when-not api-key (warn "Meetup.com API key not found.  Events page disabled."))
    (server/start (Integer. (or port "8080")) {:api-key api-key})
    ))
