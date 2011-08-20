(ns krivah.menu
	(:require [krivah.db :as db]
		[clojure.contrib.string :as cstring])
)

(defn key-menu [key value]
        (if-not (empty? value)
                (recur (assoc key (get (first value) (keyword "path")) (dissoc (first value) (keyword "path"))) (rest value))
                key))

(defn tokenize [path]
	(if (.startsWith path "/")
		(cstring/split #"/" (apply str (drop 1 path)))
		(cstring/split #"/" path)))

(defn create-damg [menu]
	(loop [x menu v {}]
		(if-not (empty? (first x))
		(recur (rest x) (update-in v (vec (tokenize (:path (first x)))) assoc :_value (first x)))
		v))	)

(defn resolve-path [menu-cache url]
		(def result (transient {}))
		(loop [x (tokenize url) path-seen []]
			(if-not (empty? x)
				(if-not (nil? (get-in menu-cache (assoc path-seen (count path-seen) (first x))))
					(recur (rest x) (assoc path-seen (count path-seen) (first x)))
					(conj! result {:_value (:_value (get-in menu-cache path-seen)), :params x}))
				(conj! result {:_value (:_value (get-in menu-cache path-seen)), :params x})))
		(persistent! result))

(defn init-menu []
	(let [m (db/list-all "menu" [:_id :title :access_perm :ns :callback :weight :description :path])]
		(create-damg m)
	)
)

(defn insert-menu-item [item]
	(db/add-item "menu" item))
