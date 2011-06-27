(ns krivah.entity
	(:require [krivah.response :as response]
		[krivah.theme :as theme]
		[krivah.formelements :as fe]
		[krivah.db :as db])
)

(defmulti extract-field-value (fn [req index] (get req (str "formtype" index))))

(defmethod extract-field-value "text" [req index] 
	(assoc {} :label (get req (str "itemlabel" index)) :machinename (get req (str "machinename" index)) :type "text")
)

(defmethod extract-field-value "select" [req index]
	(def option-count (get req (str "selectcount_formtype" index)))
	(def values (loop [vals [] i 1]
		(if-not (> i (Integer/parseInt option-count))
			(recur (assoc vals (count vals) {:option (get req (str "selectoption" i)) :value (get req (str "selectvalue" i))}) (inc i))
			vals)))
	(assoc {} :label (get req (str "itemlabel" index)) :machinename (get req (str "machinename" index)) :type "select" :options values)
)

(defmethod extract-field-value :default [req index] (println "nothing"))

(defn create [request params]
	(if (identical? (:request-method request) (keyword "post"))
	(do
		(def field-count (Integer/parseInt (get (:form-params request) "fieldcount")))
		(def fields (loop [f [] i 1 ]
		(if-not (> i field-count)
			(recur (assoc f (count fields) (extract-field-value (:form-params request) i)) (inc i))
			f)))
		(db/add-item "entities" {:label (get (:form-params request) "entity_label") :machinename (get (:form-params request) "entity_machinename") :fields fields})
		(def r (response/create-response))
			(response/set-body r (theme/theme-response request "Entity created successfully."))
		)
		(do
			(def r (response/create-response))
			(response/set-body r (theme/theme-response request (fe/html-from-file "krivah/templates/entity.html")))
		)))

(defn new-entity-instance [request params]

(println "params received " params)
(def r (response/create-response))
(response/set-body r (theme/theme-response request (fe/html-from-file "krivah/templates/entity.html")))
)
