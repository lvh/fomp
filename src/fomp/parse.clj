(ns fomp.parse
  [:require [clojure-csv.core :refer [parse-csv]]]
  [:require [clojure.string :refer [split trim]]])

(defn parse-sponsor-csv
  "Parses a sponsor CSV.

  Like a regular CSV, but throws away the second line, because that
  just contains a bunch of 'CONFIDENTIAL' markers.

  Empty rows are ignored.
  "
  [s]
  (let [[header-strs junk-line & rows] (parse-csv s)
        rows (remove #(every? empty? %) rows)
        header (map keyword header-strs)]
    (into #{} (map (partial zipmap header) rows))))

(defn parse-addresses
  "Splits a list of comma-separated things (e-mail addresses)."
  [addresses]
  (map trim (split addresses #",")))

(defn sponsors-to-recipients-with-params
  [sponsors]
  (reduce (fn [result sponsor]
            (let [recipients (parse-addresses (:Email sponsor))]
                (conj result [recipients sponsor])))
          #{}
          sponsors))
