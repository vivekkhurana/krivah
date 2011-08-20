(ns krivah.applications.accounts.accounts
	(:use [krivah core])
)

(defmodule (:name "accounts")
	(:hook :on-boot (fn [] (println "boot"))) 
	(:hook :on-update (fn [] (println "update"))))


(defmenu [{"path" "accounts/add-gl-class", "namespace" "applications.accounts.accounts", "callback" "add-gl-class"}
	{"path" "accounts/add-gl-group", "namespace" "applications.accounts.accounts", "callback" "add-gl-group"} )
