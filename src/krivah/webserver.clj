(ns krivah.webserver
	(:require [ring.adapter.jetty :as aj]
  [krivah.theme :as theme]
  [krivah.menu :as menu]
  [krivah.cache :as cache]
  [krivah.httpkernel :as kernel]
  [krivah.response :as response]
  [krivah.request :as request]
  [krivah.applications.accounts.accounts :as ac]
  [ring.middleware.file :as wf]
  [clojure.stacktrace :as st]
  [ring.middleware.file-info])
  (:use [ring.middleware params
                         keyword-params
                         nested-params
                         multipart-params
                         cookies
                         session])

  )


(cache/create-cache "menu" (menu/init-menu) )
(ac/load-module)

(defn create-response [response]
	{:status  200
      :headers {"Content-Type" "text/html"}
      :body   response}

)

(defn handler [request]
	(try
		(do
		(request/set-cur-request request)
		(response/response-to-ring-response (kernel/handle)))
		(catch Exception e (st/print-stack-trace e)))
)

(def app
	(-> handler
		(wf/wrap-file "src/theme")
		(wf/wrap-file "src/krivah/templates")
		(wrap-multipart-params)
		(wrap-params)
		(wrap-nested-params)
		(wrap-keyword-params)
		)
)

(aj/run-jetty #'app {:port 9090})
