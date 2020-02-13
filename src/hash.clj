(ns hash
  (:require [clojure.java.io :as io])
  (:import (java.security MessageDigest)
           (java.io ByteArrayOutputStream ByteArrayInputStream)
           (java.util.zip DeflaterOutputStream)))

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

(defn zip-str
  "Zip the given data with zlib. Return a ByteArrayInputStream of the zipped
  content."
  [data]
  (let [out (ByteArrayOutputStream.)
        zipper (DeflaterOutputStream. out)]
    (io/copy data zipper)
    (.close zipper)
    (ByteArrayInputStream. (.toByteArray out))))

(defn blob-data
  "function to compute the content address for a given file"
  [file]
  (let [contents (slurp file)]
    (str "blob " (count contents) "\000" contents)))

(defn print-address
  "prints hash address for given file"
  [file]
  (println (sha1-sum (blob-data file))))

(defn write-blob
  "function takes an address and writes it to the .git database"
  [file]
  (let [header+blob (blob-data file)
        address (sha1-sum header+blob)
        path-of-destination-file (str ".git/objects/" (subs address 0 2) "/" (subs address 2))]
    (io/make-parents path-of-destination-file)
    (io/copy (zip-str (slurp file)) (io/file path-of-destination-file))))

(defn hash-object
  "main function for handling the hash-object command"
  [args]
  (let [check-exists #(.exists (io/as-file %))]
    (cond
      (or (nil? (first args)) (and (= (first args) "-w") (nil? (second args)))) (println "Error: you must specify a file.")
      (not (.isDirectory (io/file ".git"))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (= (first args) "-w") (if (check-exists (second args))
                              (do (print-address (second args)) (write-blob (second args)))
                              (println "Error: that file isn't readable"))
      :else (if (check-exists (first args))
              (print-address (first args))
              (println "Error: that file isn't readable")))))

