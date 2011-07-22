(ns krivah.db
	(:require [somnium.congomongo :as congo]
		[somnium.congomongo.error :as error]))

(congo/mongo! :db "krivah" :host "127.0.0.1")

(defn uuid []
	(str (java.util.UUID/randomUUID)))


(defn add-item [col item]
	(def id (uuid))
	(congo/insert! col (assoc item :_id id))
	(if-not(nil? (error/get-last-error))
	(get (error/get-last-error) "err") 
	id)
	
)

(defn list-all [col key-vec]
	(congo/fetch (keyword col) :only key-vec)
)

(defn fetch-one-where [col qmap]
	(congo/fetch (keyword col) :where qmap)
)
