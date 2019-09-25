(defproject fake-venue-management-service "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-devel "1.6.3"]
                 [cheshire "5.8.1"]
                 [clojure.java-time "0.3.2"]]
  :ring {:handler fvms.core/handler}
  :plugins [[lein-ring "0.12.5"]])
