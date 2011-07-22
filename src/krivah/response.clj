(ns krivah.response
	(:require [krivah.theme :as theme])
)

(defprotocol IHTTPResponse
	(set-header [this key value] "set header key")
	(set-status [this status-code] "set response status code")
	(set-charset [this charset] "set charset")
	(set-body [this body] "set body"))

(defrecord HTTPResponse [headers status body]
IHTTPResponse
(set-header [this key value] 
	(dosync
		(ref-set (:headers this) (assoc (:headers this) key value))))
(set-status [this status-code]
	(assoc this :status status-code))
(set-charset [this charset]
	(assoc this (:charset (:headers this)) charset))
(set-body [this body]
	(assoc this :body body)))

(defn create-response 
([]
(HTTPResponse. (ref {"Content-Type" "text/html"}) 200 ""))
([status-code]
(HTTPResponse. (ref {"Content-Type" "text/html"}) status-code "") )
([status-code body]
(HTTPResponse. (ref {"Content-Type" "text/html"}) status-code body))
)

(defn http-response[& {:keys [header status body]
						:or {header (ref {"Content-Type" "text/html"})
							status 200
							body ""}}]
							
	(HTTPResponse. header status body))

(defn create-not-found-response []
(HTTPResponse. (ref {"Content-Type" "text/html"}) 404 "")
)


(defn response-to-ring-response [response]
	(if (instance? krivah.response.HTTPResponse response)
		(assoc {} :status (:status response), :header @(:headers response), :body (:body response))
		(throw (Error. "Response is not of type HTTPResponse"))))
