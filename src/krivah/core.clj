(ns krivah.core
	(:require [krivah.menu :as menu]))

(defn merge-with-conj [& args]
  (apply merge-with #(if (vector? %) (conj % %2) (conj [] % %2)) args))

(defn parse-fns [body]
  (apply merge-with-conj 
         (for [[one & [two three four five :as args]] body]
           {one
            (case
             one
             :name {:module two}
             :hook {two {:fn (or four three)}}
             :indexes (vec args)
             two)})))
             
(defmacro defmodule [& body]
	(let [{:keys [name hook ]} (parse-fns body)]
	`(defn ~'load-module []
		(println ~name)
		)))

(defmacro defmenu [& body]
	
	`(defn ~'load-menu []
		(doseq [x# ~@body]
			(println x#))
		))

(defmacro deffields [& body]
	`(defn ~'load-fields[]
		(println ~@body)))
			

)
