(ns krivah.theme
  (:require [net.cgrand.enlive-html :as html]
  		;[krivah.formelements :as fe]
  )
)

(defn script-element []
	(str "<script></script>")
)

(defn style-element []
	(str "<link></link>")
)

(defn select-template []
	(str "theme/jewel/index.html")
)

(html/defsnippet add-all-js (java.io.StringReader. (script-element))
[[:script]]
[req]
	[:script] (html/set-attr :src "/js/addfield.js")
)

(html/defsnippet add-all-style (java.io.StringReader. (style-element))
[[:link]]
[req]
	[:link] (html/do->
				(html/set-attr :href "/css/entity.css")
				(html/set-attr :rel "stylesheet")
				(html/set-attr :type "text/css"))
)

(defn theme-response [req content]

(def cur-template (select-template))
(println cur-template)
(html/deftemplate response-from-template cur-template []
	[(html/attr= :id "welcome")] (html/content content)
	[:head] (html/do->
			(html/append (add-all-js req))
			(html/append (add-all-style req)))
)

(apply str (response-from-template))

)


;(html/deftemplate ct "theme/jewel/index.html" []
;	[(html/id= "welcome")] (html/content (abc)))
