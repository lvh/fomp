(ns fomp.parse-test
  (:require [fomp.parse :refer :all]
            [clojure.test :refer :all]
            [clojure.string :refer [join]]))

(def header-line "Name,Email,Mailing address,Ewa's notes")
(def confidential-line ",CONFIDENTIAL,CONFIDENTIAL,")
(def lvh-line "lvh,lvh@example.test,Some place,Note note")

(defn sponsor
  [name email mailing-address notes]
  {:line (join "," [name email mailing-address notes])
   :data {(keyword "Name") name
          (keyword "Email") email
          (keyword "Mailing address") mailing-address
          (keyword "Ewa's notes") notes}})

(def lvh (sponsor "lvh" "lvh@example.test" "A place" "Note note"))
(def ewa (sponsor "ewa" "ewa@example.test" "Another place" "Another note"))
(def multi (sponsor "multi" "one@example.test, two@example.test" "Some third place" "Note"))
(def empty-line ",,,")

(def sample-sponsor-csv
  (join "\n" [header-line
              confidential-line
              (:line lvh)
              (:line ewa)]))

(def sample-sponsor-csv-with-empty-lines
  (join "\n" [header-line
              confidential-line
              empty-line
              empty-line
              (:line ewa)
              empty-line
              empty-line
              (:line lvh)
              empty-line]))

(deftest parse-sponsor-csv-tests
  (testing "parse header and rows correctly"
    (is (= (parse-sponsor-csv sample-sponsor-csv)
           #{(:data lvh)
             (:data ewa)})))
  (testing "parse header and rows correctly, even with empty lines"
    (is (= (parse-sponsor-csv sample-sponsor-csv-with-empty-lines)
           #{(:data lvh)
             (:data ewa)}))))

(deftest sponsors-to-recipients-with-params-tests
  (testing "turns two sponsors into an appropriate seq"
    (is (= (sponsors-to-recipients-with-params #{(:data lvh)
                                                 (:data ewa)})
           #{[[(:Email (:data lvh))] (:data lvh)]
             [[(:Email (:data ewa))] (:data ewa)]})))
  (testing "handling of multiple e-mail addresses"
    (is (= (sponsors-to-recipients-with-params #{(:data multi)})
           #{[["one@example.test" "two@example.test"] (:data multi)]}))))

(deftest parse-addresses-tests
  (testing "one address gets turned into a vector of one address"
    (is (= (parse-addresses "test@example.com")
           ["test@example.com"])))
  (testing "multiple addresses get split and trimmed"
    (is (= (parse-addresses "test@example.com, test2@example.com")
           ["test@example.com" "test2@example.com"]))))
