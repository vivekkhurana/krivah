(ns krivah.cache

)

(defn create-cache [key value]

	(if (nil? (resolve (symbol key)))
		(intern 'krivah.cache (symbol (str key "-cache")) (ref value)))
	
)

(defn clear-cache [cache]
	(dosync
		(ref-set (ref (symbol cache)) {}))
)

(defn update-cache [cache value]
	(dosync
		(ref-set @(resolve (symbol cache)) value))
)

(defn update-cache-key [cache-name key value]
	(dosync
		(ref-set @(resolve (symbol cache-name)) (assoc @@(resolve (symbol cache-name)) (keyword key) value)))
)

(defn get-cache [key]
	
	@@(ns-resolve 'krivah.cache (symbol key))
)


