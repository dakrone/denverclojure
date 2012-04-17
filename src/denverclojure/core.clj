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

(defpage "/startingclojure" []
  (html [:h1 "Starting Clojure!"]
        [:p "So you've decided to dabble in Clojure?
             Before you get started there are a few things you'll need
             to get you going!"]
        [:h3 "Installing Leiningen:"]
        [:ol "Leiningen makes working with Clojure bareable. Most people think
             its the bee's knees and we'll be using it to make and use projects
             at our meetups. You can find the link to 
             leiningen below. Install instructions are also located
             on this page."]
        [:ol (link-to "https://github.com/technomancy/leiningen" "Leiningen")]
        [:h3 "Installing Emacs and Marmalade:"]
        [:ol "You can use any text editor you wish to edit your clojure files,
             but  we'll be using emacs and several of it's
             extensions at the meetups. The rest of this tutorial involves setting
             up emacs to work with Clojure, so if you wish to use a different text
             editor, the remainder of this tutorial is probably not worth your time."]
        [:ol (link-to "http://emacsformacosx.com/" "Emacs for OSX")
         " You'll probably be safe installing v24."]
        [:ol (link-to "https://code.google.com/p/emacs-for-windows/downloads/list"
                      "Emacs for Windows")]
        [:ol "Got it installed? Great, now we're going to install a file from "
         (link-to "http://marmalade-repo.org/" "Marmalade")
         " this will allow you to easily download the extensions we need
         (as well as others) to work with Clojure. Open up a blank buffer
         in emacs by typing \"emacs\" in your terminal and then copy and
         paste the following into the blank buffer."]
        [:ol
         [:pre "(progn "
          [:ol "(switch-to-buffer"]
          [:ol "(url-retrieve-synchronously \"http://repo.or.cz/w/emacs.git/blob_plain/1a0a666f941c99882093d7bd08ced15033bc3f0c:/lisp/emacs-lisp/package.el\"))"]
          [:br "(package-install-from-buffer  (package-buffer-info) 'single))"]]]
        [:ol "Now type \"M-x eval-buffer\" (when dealing with emacs, M and C refer to
              Meta and Control, respectively). This should write the file from
              Marmalade and then install it into your emacs file. In emacs type in
              \"M-x package-list-packages\" and find clojure-mode. Place an \"i\"
              next to the package and hit the \"x\" key and accept the installation. 
              You're almost ready to start hacking!"]
        [:ol [:center [:h4 "At this point, I would say if you're not familiar with some
              of the basic commands with leiningen (especially the \"new\" command) or
              emacs, it would be worth your time to play around and read the tutorials
              on how to use these. From here on out, we're going to assume you know
              how to create a new project."]]]
        [:h3 "Gettin' Swanky:"]
        [:ol "Open up your core.clj and type in \"M-x clojure-jack-in\".
              Assuming everything went right, this should open up another buffer
              with a REPL. Compile your file using \"C-c C-k\" then change the
              namespace (so the REPL has access to your functions) by typing in
              \"C-c M-p\" and accepting your namespace. Now you're ready!
              Of course you'll need to make some functions in your core.clj file
              in order to have any fun and that may be beyond the scope of this
              tutorial but you're now set up to effeciently process Clojure
              functions in emacs."]))

(defpartial user [n]
  [:p [:h2 (str "Hello " n)]])

(defpartial users [names]
  (for [name names]
    (user name)))

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
              {:name "Michael"
               :github "http://www.github.com/mikehunsinger"}])

(def links [{:url "http://overtone.github.com/"
             :desc "A music creation using Clojure"
             :link-text "Overtone"}
            {:url "http://webnoir.org/"
             :desc "Noir, a web framework"
             :link-text "Noir"}
            {:url "https://github.com/cgrand/enlive"
             :desc "A templating system for Clojure with CSS-like selectors"
             :link-text "Enlive"}])

(def clojuregoodness [{:url "startingclojure"
                       :desc "Startup guide for installing and getting an
                              environment set up to run clojure"
                       :link-text "Starting Clojure"}
                      {:url "/html"
                       :link-text "foo"}])

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
   [:h2 "Clojure Goodness"]
   [:ul#clojuregoodness (map make-link clojuregoodness)]
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
