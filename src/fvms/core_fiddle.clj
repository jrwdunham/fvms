(ns fvms.core-fiddle
  (:require [fvms.core :refer :all]
            [ring.adapter.jetty :as jetty]))

(comment

  (* 8 8)

  (x 4)

  (jetty/run-jetty handler {:port 13000})

)

