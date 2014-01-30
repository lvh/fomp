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

(defn send-many
  "Sends a given template string to many recipients using SMTP configuration
  smtp-conf and common message parameters (:from, :subject are pretty
  important) common-mail-params.

  "
  [tpl smtp-conf common-mail-params recipients-with-params]
  (let [send (fn [recipients body]
               (ps/send-message ^smtp-conf
                             (into common-mail-params
                                   {:to recipients
                                    :body body})))]
    (reduce (fn [results [recipients params]]
              (let [body (make-body (ms/render tpl params))]
                (conj results (into {:to recipients}
                                    (send recipient body))))))))
