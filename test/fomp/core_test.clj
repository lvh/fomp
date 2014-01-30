(ns fomp.core-test
  (:require [clojure.test :refer :all]
            [fomp.core :refer :all]))

(deftest make-body-tests
  (testing "empty body"
    (is (= (make-body "")
           [{:type "text/plain"
             :content ""}
            {:type "text/html; charset=utf-8"
             :content ""}]))))

(def success-result {:code 0, :error :SUCCESS, :message "message sent"})

(defn make-test-send
  "Makes a send function for testing."
  []
  (let [sent (atom [])]
    [sent
     (fn [recipients body]
       (swap! sent #(conj % [recipients body]))
       success-result)]))

(defn fake-send-many
  "Set up send-many with a fake send function. Returns sent messages and output."
  [tpl recipients-with-params]
  (let [[sent send] (make-test-send)
        result (send-many send tpl recipients-with-params)]
    [@sent result]))

(deftest send-many-tests
  (testing "no messages"
    (let [[sent result] (fake-send-many "template" [])]
      (is (= sent []))
      (is (= result []))))
  (testing "several messages, some with multiple recipients"
    (let [params [["alice@example.test" {:name "Alice"}]
                  ["bob@example.test" {:name "Bob"}]
                  [["carol@example.test" "dave@example.test"] {:name "Carol and Dave"}]]
          [sent result] (fake-send-many "Hello {{ name }}" params)]
      (is (= sent [["alice@example.test"
                    (make-body "Hello Alice")]
                   ["bob@example.test"
                    (make-body "Hello Bob")]
                   [["carol@example.test" "dave@example.test"]
                    (make-body "Hello Carol and Dave")]]))
      (is (= result [(into success-result {:to "alice@example.test"})
                     (into success-result {:to "bob@example.test"})
                     (into success-result {:to ["carol@example.test" "dave@example.test"]})])))))
