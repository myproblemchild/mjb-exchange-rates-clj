(ns google-sheets.core
  (:require
	[clj-http.client :as client]
	[org.httpkit.server :refer [run-server]]
	[cheshire.core :refer :all])
  (:gen-class))

(def api-key (System/getenv "GOOGLE_API_KEY"))
(def server-port (Integer/parseInt (or (System/getenv "SERVER_PORT") "8080")))
(def spreadsheet-id (System/getenv "SPREADSHEET_ID"))
(def url-template "https://sheets.googleapis.com/v4/spreadsheets/%s/values/Sheet1!A1:B?key=%s")

;; Final request URL to fetch exchange rates.
(def request-url (format url-template spreadsheet-id api-key))

(defn fetch-json-from-url [url] (client/get url {:as :json}))

(defn make-exchange-rates-map [fetched-exchange-pairs]
 (->>
    fetched-exchange-pairs ;; Contains spreadsheet data like [["USD" "116.4192"] ["EUR" "129.2423"] ["GBP" "152.5809"] ... ]
    flatten                ;; Flattens the pairs into [["USD" "116.4192" "EUR" "129.2423" "GBP" "152.5809" ... ]]
    (apply hash-map)       ;; Creates a rates map like {"SGD" "85.8865", "AUD" "86.9514", "EUR" "129.2423" ... }
 )
)

(defn build-response-map [exchange-rates-map]
  {
    "base" "JPY",
    "rates" exchange-rates-map
  }
)

(defn fetch-exchange-rates-and-build-output-map []
  (->>
    request-url
    fetch-json-from-url
    :body
    :values
    make-exchange-rates-map
    build-response-map
  )
)

(defn serve-request [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (generate-string (fetch-exchange-rates-and-build-output-map))
  }
)

(defn -main [& args]
  (run-server serve-request {:port server-port})
  (print "Server running on port " server-port)
)
