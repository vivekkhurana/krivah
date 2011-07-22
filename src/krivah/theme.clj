(ns krivah.theme
  (:require [net.cgrand.enlive-html :as html]
  		;[krivah.formelements :as fe]
  )
)

(defn select-template []
	(str "theme/zen/index.html"))

(defn add-all-js [media]
	(map #(assoc {} :tag (keyword "script") :attrs % :content nil) (:scripts media)))

(defn add-all-style [media]
	(map #(assoc {} :tag (keyword "link") :attrs % :content nil) (:style media)))

(defn theme-response [req content & media]

	(def cur-template (select-template))

	(html/deftemplate response-from-template cur-template []
	[(html/attr= :id "welcome")] (html/content content)
	[:head] (html/do->
			(html/append (add-all-js (first media)))
			(html/append (add-all-style (first media)))))

	(apply str (response-from-template)))
