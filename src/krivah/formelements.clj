(ns krivah.formelements
          (:require [net.cgrand.enlive-html :as html]
          	[krivah.request :as request]
          	[krivah.theme :as theme]
          	[krivah.response :as response]
          	
          )
)

(def input-element (str "<input type=\"\" id=\"\" class=\"\" name=\"\">"))

(def select-element (str "<select id=\"\" name=\"\" class=\"\"><option></option></select>"))

(def option-element (str "<option value=\"\"></option>"))

(defn apply-attr [val]
  (apply html/set-attr (apply concat val)))

(defmulti fill-form-widget (fn [node value] (:tag node)))

(defmethod fill-form-widget :input [node value]
	((html/set-attr :value value) node))

(defmethod fill-form-widget :textbox [node value]
	((html/content value) node))
	
(defmethod fill-form-widget :select [node value]
	(html/at node [:option] (fn [node value]
		(if (= (get-in node [:attrs :value]) "c")
		    ((html/set-attr :selected "selected") node)
    		node))))

(defn fill-form [form values]
	(loop [f form x values]
		(if-not(empty? x)
			(recur (html/at 
				[(html/id= (:id (first x)))] (fill-form-widget (:value (first x)))) (rest x))
				f)))
				
(defn html-from-file [filepath]
	(html/html-resource filepath)
)

 
(html/defsnippet option-element-snip (java.io.StringReader. select-element)
    [[:option]]
    [value]
    (let [val (get value :value)
          attr (dissoc value :value)]
    [[:option]] (html/do->
                (html/content (get value :value))
                (apply-attr attr)
        )
      )
)

(html/defsnippet select-element-snip (java.io.StringReader. select-element)
  [[:select]]
  [value]
  (let [opt (get value :options)
        val (dissoc value :options)]
  [:select] (html/do->
             (apply-attr val)
             (html/content (map #(option-element-snip %) opt)) 
            )
        )
)

(defn as-str
  ([] "")
  ([x] (if (instance? clojure.lang.Named x)
         (name x)
         (str x)))
  ([x & ys]
     ((fn [^StringBuilder sb more]
        (if more
          (recur (. sb  (append (as-str (first more)))) (next more))
          (str sb)))
      (new StringBuilder ^String (as-str x)) ys)))

(defn text-plain
  "Change special characters into HTML character entities."
  [text]
  (.. ^String (as-str text)
    (replace "&"  "&amp;")
    (replace "<"  "&lt;")
    (replace ">"  "&gt;")
    (replace "\"" "&quot;")))

(defmulti form-validator (fn [name element value]
		(if-not (nil? (get-in element [:validator :type]))
				(get-in element [:validator :type])
				(:type element))))

(defmulti tag-value (fn [tag-key element value]
		(:type element)))
		
(defmethod form-validator "text" [name element value]
	(if (and (true? (:required element)) (nil? value))
		(assoc {} (keyword name) {:content (:message_required (:validator element) (str (:label element) "This is a required field"))})
		nil))
(defmethod form-validator "numeric" [name element value]
	(if (and (true? (:required element)) (nil? value))
		(assoc {} name {:content (:message_required (:validator element) (str (:label element) "This is a required field"))})
		(if-not (number? value)
			(assoc {} name {:content "Please enter a numeric value"})
			nil)))
(defmethod form-validator "email" [name element value]
	(if (and (true? (:required element)) (nil? value))
		(assoc {} name {:content (:message_required (:validator element) "This is a required field")})
		(if (nil? (re-matches #"^[a-z0-9,!#\$%&'\*\+/=\?\^_`\{\|}~-]+(\.[a-z0-9,!#\$%&'\*\+/=\?\^_`\{\|}~-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*\.([a-z]{2,})$" value))
			(assoc {} name {:content (:message_required (:validator element) "Please enter a valid email address")})
			nil))) 
			
(defmethod form-validator "custom" [name element value]
	(try
		(use (:ns (:validator element)))
		(catch Exception e "Error loading namespace "))

)
(defmethod form-validator :default [tag-key element value]
	nil)
	
(defmethod tag-value "radios" [tag-key element value]
	(if (identical? (class value) java.lang.String)
		(assoc {} tag-key (assoc element :value (list value)))
		(assoc {} tag-key (assoc element :value value))))

(defmethod tag-value "checkboxes" [tag-key element value]
	(if (identical? (class value) java.lang.String)
		(assoc {} tag-key (assoc element :value (list value)))
		(assoc {} tag-key (assoc element :value value))))
		
(defmethod tag-value "select" [tag-key element value]
	(if-not (identical? (class value) java.lang.String)
		(assoc {} tag-key (assoc element :value value))
		(assoc {} tag-key (assoc element :value (list value)))))

(defmethod tag-value "textarea" [tag-key element value]
	(assoc {} tag-key (assoc element :content (list value))))
		
(defmethod tag-value :default [tag-key element value]
	(assoc {} tag-key (assoc element :value value)))

(defn validate-form [form form-values]

	(apply merge (map #(form-validator % (get form %) (get form-values (name %))) (keys form)))
)

(defn merge-form-with-values [form form-values]
	(apply merge (map #(tag-value % (get form %) (get form-values (name %))) (reverse (keys form))))
)

(defn tag-li [elem]
	{:tag :li, :attrs (:attrs elem), :content (list (:content elem))})
	
(defn form-required-tag []
	{:tag :span, :attrs {:class "form-required", :title "This field is required"}, :content '("* ")})
	
(defn list-form-errors [errors]
	(list {:tag :ul, :content (map #(tag-li (get errors %) ) (keys errors))}))
	
(defn form-error-node [form]
	(if-not (nil? (:errors form))
	(list {:tag :div, :attrs {:class "form-error"}, :content (list-form-errors (:errors form))})
	nil))

(defn form-build-id []
	(assoc {} :tag :input, :attrs {:id "form-build-id" :type "hidden" :value (str (java.util.UUID/randomUUID))}))

(defn tag-label [label-text label-for required]
	(if (true? required)
		(assoc {} :tag :label, :attrs {:for label-for}, :content (list (str label-text " ") (form-required-tag)))
		(assoc {} :tag :label, :attrs {:for label-for}, :content (list label-text))))

(defn tag-radio [name label value checked]
	(def n (assoc {} :tag :input, 
		:attrs {:id name, :name name, :type "radio", :value value},  
		:content (list label)))
		(if-not (nil? checked)
			(assoc n :attrs (assoc (:attrs n) :checked "checked"))
			n))
(defn tag-checkbox [name label value checked]
	(def n (assoc {} :tag :input, 
		:attrs {:id name, :name name, :type "checkbox", :value value},  
		:content (list label)))
		(if-not (nil? checked)
			(assoc n :attrs (assoc (:attrs n) :checked "checked"))
			n))
			
(defn tag-option [name value selected]

	(def n (assoc {} :tag :option, 
		:attrs {:value name},  
		:content (list value)))
		(if-not (nil? selected)
			(assoc n :attrs (assoc (:attrs n) :selected "checked"))
			n))	

(defn tags-option [options value]
	 (map #(tag-option % (get options %) (some #{%} value)) (keys options)))
	 
(defmulti create-tag (fn [tag] (:type tag)))

(defmethod create-tag "text" [tag]
	(list (assoc {} :tag :input, :attrs {:type "text", :id (:id tag) :name (:id tag) :value (:value tag)})))
	
(defmethod create-tag "password" [tag]
	(list (assoc {} :tag :input, :attrs {:type "password", :id (:id tag) :name (:id tag) :value (:value tag)})))

(defmethod create-tag "textarea" [tag]
	(list (assoc {} :tag :textarea, :attrs {:id (:id tag) :name (:id tag)}, :content (list (:value tag)))))
	
(defmethod create-tag "radios" [tag]
	 (flatten (map #(tag-radio (:id tag) (get (:options tag) %) % (some #{%} (:value tag))) (keys (:options tag)))))

(defmethod create-tag "checkboxes" [tag]
	 (flatten (map #(tag-checkbox (:id tag) (get (:options tag) %) % (some #{%} (:value tag))) (keys (:options tag)))))

(defmethod create-tag "select" [tag]
	(list (assoc {} :tag :select, :attrs {:id (:id tag) :name (:id tag)}, :content (tags-option (:options tag) (:value tag)))))

(defn create-node [tag name]
	(assoc {} 
		:tag :div, 
		:attrs {:class "form-item" :id (str "edit-" (:id tag) "-wrapper")}, 
		:content (concat (list (tag-label (:label tag) (:id tag) (:required tag))) (create-tag tag))))
		
(defn create-form-node 
([]
(assoc {} :tag :form,  :attrs {:action (:uri (request/cur-request)) :method "POST"}, :content (list {:tag :input, :attrs {:id "submit" :type "submit"}} (form-build-id))))
([form]
(assoc {} :tag :form, :attrs {:action (:action form) }, :content (list (form-build-id)))))

(defn create-form [form]

	(def form-elements (map #(create-node (get form %) %) (keys (dissoc form :form :errors))))

	(def form-node (create-form-node)) 
	
	(list (assoc form-node :content (concat (form-error-node form) form-elements (:content form-node)))))

(defn manage-form [form f]

 (if (identical? (:request-method (request/cur-request)) (keyword "post"))
 		(let [vform (validate-form form (:form-params (request/cur-request)))]
 			(if (nil? vform)
 				(f (:form-params (request/cur-request)))
 				(response/http-response :body (theme/theme-response (request/cur-request) (create-form (merge-form-with-values (assoc form :errors vform) (:form-params (request/cur-request))))))))
 		(response/http-response :body (theme/theme-response (request/cur-request) (create-form form)))))


