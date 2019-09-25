(ns fvms.core)

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello My Awesome World"})
