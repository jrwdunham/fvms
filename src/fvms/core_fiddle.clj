(ns fvms.core-fiddle
  (:require [clojure.string :as str]
            [fvms.core :refer :all]
            [ring.adapter.jetty :as jetty]))

(comment

  (* 8 8)

  (filter-requests
   [{"a" 2}
    {"a" 3}]
   {"a" 2})

  (filter-requests
   [{"a" 2}
    {"a" 3}]
   {})

  (assoc-in {} [:a :b] 2)

  (every? identity [true false])

  (every? identity [true true])

  (query-string->map "a=2")

  (->> "abc"
       seq
       (apply str)
       keyword)

  (uri->kw "/in/qu-iries/")

  (query-string->map "")

  (->> (str/split "" #"&")
       (map #(str/split % #"="))
       ;; (into {})
       )

  (and true "abc")

  (let [prefix "/requests/"
        s "/zrequests/abc"]
    (if (str/starts-with? s prefix)
      (->> s (drop (count prefix)) (apply str))
      s))

  (empty? nil)

  (empty? "")

  (empty? (str/trim "  "))

  (empty? (str/trim nil))


  (into {} [[1 2]])

)

