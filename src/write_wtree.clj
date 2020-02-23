(ns write-wtree
  (:require [clojure.java.io :as io]
            [hash-handler :as hh]
            [hash-object :as ho]
            [clojure.string :as str]
            [javax.xml.bind.DatatypeConverter]))

(defn write-tree-object
  [tree]
  (let [header+tree (str "tree " (count tree) "\000" tree)
        address (hh/sha1-sum header+tree)
        path-of-destination-file (str ".git/objects/" (subs address 0 2) "/" (subs address 2))]
    (io/make-parents path-of-destination-file)
    (println header+tree)
    (println address)
    (io/copy (ho/zip-str header+tree) (io/file path-of-destination-file))))

(defn hash->byte-array
  [hash]
  (let [splits (map (partial apply str) (partition-all 2 hash))
        bytes (for [colloc splits] (read-string (str "0x" colloc)))]
   (byte-array bytes)))

(defn byte-array->hash
  [byte-array-string]
  (String. (.getBytes byte-array-string)))

(defn generate-blob-entry
  "generates a blob entry for use in a tree"
  [file]
  (ho/write-blob (.getAbsolutePath file))
  (str "100644 " (.getName file) "\000" (new String (hash->byte-array (hh/sha1-sum (hh/blob-data file))))))

(defn generate-tree-entry
  "generate tree entry"
  [entries]
  (let [entries-info (map (fn [string] (let [null-split (str/split string #"\000")]
                                               [(second null-split) string])) entries)
        alpha-order (sort-by second entries-info)
        tree-entry (apply str (map second alpha-order))]
    (write-tree-object tree-entry)
    (str "04000 " (count tree-entry) "\000" tree-entry)))


(defn gen-tree
  "Function for recursively generating a tree given a directory"
  [dir]
  (let [files (file-seq dir)
        entries (for [file (rest files)] (if (.isDirectory file)
                                    (gen-tree file)
                                    (generate-blob-entry file)))]
    (generate-tree-entry entries)))

(defn write-wtree
  "Function handles the write-wtree command"
  [args]
  (cond
    (> (count args) 0) (println "Error: write-wtree accepts no arguments")
    (not (.isDirectory (io/file ".git"))) (println "Error: could not find database. (Did you run `idiot init`?)")
    :else (gen-tree (io/file "dir"))))

(write-wtree [])
(for [x (.getBytes "çË·\u001A\\r[f½q\u0016V;9Z˜¨\u001EPùˆ")] (println (char x)))
(print (new String (hash->byte-array "e7cbb71a0d5b66bd7116563b395a98a81e50f988")))
(count (.getBytes "\u007Fè‰³‰\u0012”×oÄ5ACAB¢¶\u001F¾»"))

