(ns fomp.core
  [:require [clostache.parser :as ms]]
  [:require [postal.core :as ps]]
  [:require [markdown.core :as md]])

(defn make-body
  "Creates a body from markdown text suitable for giving to postal."
  [md-body-text]
  [{:type "text/plain"
    :content md-body-text}
   {:type "text/html; charset=utf-8"
    :content (md/md-to-html-string md-body-text)}])

(defn make-send
  "Makes a send function suitable for use with send-many.

  common-mail-params should at least include :subject and :from.
  "
  [smtp-conf common-mail-params]
  (fn [recipients body]
    (ps/send-message ^smtp-conf
                     (into common-mail-params
                           {:to recipients
                            :body body}))))

(defn send-many
  "Sends a given template to many recipients.

  send is a side-effectful fn [recipients body] that will send out some
  e-mail.
  "
  [send tpl recipients-with-params]
  (reduce (fn [results [recipients params]]
            (let [body (make-body (ms/render tpl params))]
              (conj results (into {:to recipients}
                                  (send recipients body)))))
          []
          recipients-with-params))
