(ns fomp.parse-test
  (:require [fomp.parse :refer :all]
            [clojure.test :refer :all]
            [clojure.string :refer [join]]))

(def sample-sponsor-csv
  (join "\n" ["Name,E-mail,Mailing address,Ewa's notes"
              ",CONFIDENTIAL,CONFIDENTIAL,"
              "lvh,lvh@example.test,Some place,Note note"
              "ewa,ewa@example.test,Some place,Note note"]))

(deftest parse-sponsor-csv-tests
  (testing "parse header and rows correctly"
    (is (= (parse-sponsor-csv sample-sponsor-csv)
           ({:Ewa's notes "Note note",
             :Mailing address "Some place",
             :E-mail "lvh@example.test",
             :Name "lvh"}
            {:Ewa's notes "Note note",
             :Mailing address "Some place",
             :E-mail "ewa@example.test",
             :Name "ewa"})))))
