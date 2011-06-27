(ns krivah.httpkernel
	(:require [krivah.cache :as cache]
		[krivah.response :as response]
		[krivah.request :as request]
		[clojure.stacktrace :as st]
	[krivah.menu :as menu])
)

(defn handle-exception [exception req]
	(response/create-response 500 (str "Error occured" exception)) 
)

(defn handle-not-found []
 (response/create-response 404)
)

(defn call-controller [menu-map request]

 	(try (use (symbol (str (:ns (:_value menu-map)))))
 		(catch Exception e (prn "Error loading namespace: " e)))
	(try ((ns-resolve (symbol (str (:ns (:_value menu-map)))) (symbol (str (:callback (:_value menu-map))))) request (:params menu-map))
		(catch Exception e (prn "Error " (st/print-stack-trace e))))
)

(defn filter-response [response]
	response
)

(defn handle-raw [request]
	
	(def req-map (menu/resolve-path (cache/get-cache "menu-cache") (:uri request)))
	(if-not (empty? req-map)
	(do
		
		(def resp (call-controller req-map request))
	
		(if (instance? krivah.response.HTTPResponse resp)
			(filter-response resp)
			(resp)
		))
		(handle-not-found)
	)
)



(defn handle []

	(try (handle-raw (request/cur-request))
		(catch Exception e (handle-exception e request/cur-request)))
)
