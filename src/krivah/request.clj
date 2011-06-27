(ns krivah.request)

(def _request (ref {}))

(defn set-cur-request [request]
	(dosync
		(ref-set _request request)))

(defn cur-request []
	@_request)
