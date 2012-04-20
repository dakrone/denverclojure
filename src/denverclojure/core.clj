(ns denverclojure.core
  (:require [clojure.java.io :refer [resource]]
            [noir.core :refer :all]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [hiccup.form :refer :all]
            [hiccup.element :refer :all]
            [cheshire.core :refer :all]
            [noir.server :as server]
            [clj-http.client :as http]))

(def api-key (.trim (slurp (resource "key"))))

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

;; {:group
;;  {:id 1352976,
;;   :group_lat 37.380001068115234,
;;   :name "The Bay Area Clojure User Group",
;;   :group_lon -122.08000183105469,
;;   :join_mode "open",
;;   :urlname "The-Bay-Area-Clojure-User-Group",
;;   :who "Clojure programmers"},
;;  :status "upcoming",
;;  :maybe_rsvp_count 0,
;;  :name "The Bay Area Clojure User Group Late/MV Monthly Meetup",
;;  :utc_offset -25200000,
;;  :yes_rsvp_count 29,
;;  :waitlist_count 0,
;;  :created 1334077505000,
;;  :updated 1334599365000,
;;  :time 1334883600000,
;;  :event_url
;;  "http://www.meetup.com/The-Bay-Area-Clojure-User-Group/events/59990302/",
;;  :id "dshxscyqgbzb",
;;  :description
;;  "<p><strong>Julio: Exploring data with Clojure and Incanter: �A Hands On Introduction</strong></p>\n<p>�</p>\n<p>Incanter is a Clojure-based, R-like platform for statistical computing�and graphics. In this meeting we'll have an overview and short hands�on intro to Incanter. Then we will open it up to exploration,�discussion and experimentation so bring your laptops and data sets.</p>\n<p>�</p>\n<p>More information on Incanter can be found at <a href=\"http://incanter.org/\">http://incanter.org/</a></p>\n\n"}

(defn format-event [event]
  (select-keys event [:group :time :name :event_url]))

(defn get-events
  "Gets the events from Meetup.com"
  []
  ;; [{:name "Website Hackery" :description "Do some things."
  ;;   :date (java.util.Date.)}
  ;;  {:name "History of Lisp" :description "Do some other things."
  ;;   :date (java.util.Date. (long 0))}]

  (let [results (get-in (http/get "http://api.meetup.com/2/events"
                                  {:query-params {:group_id "1674002"
                                                  :key api-key}
                                   :as :json})
                        [:body :results])]
    (map format-event results)))

(defpartial event [e]
  [:li [:h3 (str (-> e :group :name))] (:name e)])

(defpage "/events" []
  (html "Events:"
        [:ul (map event (get-events))]))

(def members [{:name "Lee"
               :github "http://github.com/dakrone"}
              {:name "Daniel Glauser"
               :github "https://github.com/danielglauser"}
              {:name "J......"
               :github "http://kitties.com"}
              {:name "Gregory"
               :github "http://github.com/gregoryg"}
              {:name "Bart"
               :github "http://github.com/kasterma"}])

(def links [{:url "http://overtone.github.com/" :desc "A music creation using Clojure" :link-text "Overtone"}
            {:url "http://webnoir.org/" :desc "Noir, a web framework"}
            {:url "https://github.com/cgrand/enlive" :desc "A templating system for Clojure with CSS-like selectors" :link-text "Enlive"}

            ])



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
  (server/start (Integer. (or port "8080"))))

;; (defonce denverclojure-server (server/start 8080))
