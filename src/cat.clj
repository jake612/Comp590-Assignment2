(ns cat
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.util.zip InflaterInputStream)
           (java.io ByteArrayOutputStream)))

(defn unzip
  "Unzip the given data with zlib. Pass an opened input stream as the arg. The
  caller should close the stream afterwards."
  [input-stream]
  (with-open [unzipper (InflaterInputStream. input-stream)
              out (ByteArrayOutputStream.)]
    (io/copy unzipper out)
    (->> (.toByteArray out)
         (map char)
         (apply str))))

(defn open-file
  "function which gets the file contents from a hashed object"
  [path]
  (let [open #(with-open [input (-> % io/file io/input-stream)]
                (unzip input))]
    (nth (str/split (open path) #"\000") 1)))
(defn cat-file
  "function for handling cat-file command"
  [args]
  (let [address (second args)
        switch (first args)
        get-path #(str ".git/objects/" (subs % 0 2) "/" (subs % 2))]
    (cond
      (not (.isDirectory (io/file ".git"))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (not= switch "-p") (println "Error: the -p switch is required")
      (nil? address) (println "Error: you must specify an address")
      (not (.exists (io/as-file (get-path address)))) (println "Error: that address doesn't exist")
      :else (print (open-file (get-path address))))))