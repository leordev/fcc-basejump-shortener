(defproject shortener "0.1.0-SNAPSHOT"
  :description "FCC Project URL Shortener"
  :url "http://github.com/leordev/fcc-basejump-shortener"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [http-kit "2.2.0-beta1"]
                 [ring/ring-devel "1.5.0"]
                 [ring/ring-core "1.5.0"]
                 [cheshire "5.6.3"]
                 [commons-validator/commons-validator "1.5.1"]
                 [environ "1.0.3"]
                 [compojure "1.5.1"]
                 [org.clojure/java.jdbc "0.6.2-alpha1"]
                 [mysql/mysql-connector-java "5.1.6"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "shortener.jar"
  :profiles {:production {:env {:production true}}})
