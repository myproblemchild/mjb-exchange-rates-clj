(ns google-sheets.core-test
  (:require [clojure.test :refer :all]
            [google-sheets.core :refer :all]))

(def example-e2e-result
  {"base" "JPY",
   "rates" {
            "SGD" "86.0421",
            "AUD" "87.2506",
            "EUR" "129.2386",
            "GBP" "152.8112",
            "HKD" "14.9062",
            "CAD" "93.1443",
            "USD" "116.7936"
            }
   })

(def example-spreadsheet-json-response
  {
   "range" "Sheet1!A1:B1000",
   "majorDimension" "ROWS",
   "values" [
             ["USD" "116.7936"]
             ["EUR" "129.2386"]
             ["GBP" "152.8112"]
             ["AUD" "87.2506"]
             ["CAD" "93.1443"]
             ["HKD" "14.9062"]
             ["SGD" "86.0421"]
             ]
   })

(deftest e2e-test
  (testing "e2e scenario with mocked out get request"
    (with-redefs [fetch-json-from-url (fn [_] example-spreadsheet-json-response)]
      (is (= example-e2e-result
             (fetch-exchange-rates-and-build-output-map)
           )))))

(deftest format-request-url
  (testing "Check that formatting request URL produces a sensible result"
    (is
     "https://sheets.googleapis.com/v4/spreadsheets/abcdef/values/Sheet1!A1:B?key=google_api_key"
     (format url-template "abcdef" "google_api_key"))))
