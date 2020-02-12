(ns hash
  (:require [clojure.java.io :as io])
  (:import (java.security MessageDigest)))

(defn sha1-hash-bytes [data]
  (.digest (MessageDigest/getInstance "sha1")
           (.getBytes data)))

(defn byte->hex-digits [byte]
  (format "%02x"
          (bit-and 0xff byte)))

(defn bytes->hex-string [bytes]
  (->> bytes
       (map byte->hex-digits)
       (apply str)))

(defn sha1-sum [header+blob]
  (bytes->hex-string (sha1-hash-bytes header+blob)))

(defn content-address
  "function to compute the content address for a given file"
  [file]
  )

(defn write-blob
  "function takes an address and writes it to the .git database"
  [file-addr])

(defn hash-object
  "main function for handling the hash-object command"
  [args]
  (let [check-exists #(.exists (io/as-file %))]
    (cond
      (not (.isDirectory (io/file ".git"))) (println "Error: could not find database. (Did you runidiot init?)")
      (= (second args) "-w") (if (check-exists (second args))
                               (println (content-address (second args)))
                               (println "Error: that file isn't readable"))
      :else (if (check-exists (first args))
              (print (content-address (first args)))
              (println "Error: that file isn't readable"))
      )
    ))
