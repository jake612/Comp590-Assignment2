(ns commit-tree
  (:require [clojure.java.io :as io]
            [git]
            [write-wtree :as wt]
            [byte-array :as ba])
  (:import (java.io ByteArrayOutputStream)
           (java.util.zip InflaterInputStream)))

(def author_committer "Linus Torvalds <torvalds@transmeta.com> 1581997446 -0500")

(defn unzip
  "Unzip the given file's contents with zlib."
  [path]
  (with-open [input (-> path io/file io/input-stream)
              unzipper (InflaterInputStream. input)
              out (ByteArrayOutputStream.)]
    (io/copy unzipper out)
    (.toByteArray out)))

;; Note that if given binary data this will fail with an error message
;; like:
;; Execution error (IllegalArgumentException) at ,,,.
;; Value out of range for char: -48
(defn bytes->str [bytes]
  (->> bytes (map char) (apply str)))

(defn split-at-byte [b bytes]
  (let [part1 (take-while (partial not= b) bytes)
        part2 (nthrest bytes (-> part1 count inc))]
    [part1 part2]))

(defn file-path
  [file-name]
  (str ".git/objects/" (subs file-name 0 2) "/" (subs file-name 2)))

(defn get-object-type
  [address]
  (->> (file-path address)
       unzip
       (split-at-byte (byte 0x20))
       first
       bytes->str))

(defn commit-object
  [commits-str author-str tree-addr message]
  (let [commit-format (str "tree %s\n"
                           "%s"
                           "author %s\n"
                           "committer %s\n"
                           "\n"
                           "%s\n")
        commit-str (format commit-format
                           tree-addr
                           commits-str
                           author-str
                           author-str
                           message)]
    (format "commit %d\000%s"
            (count commit-str)
            commit-str)))

(defn which-true
  "given a function for testing and a sequence, returns the first value that evaluates to false"
  [func seq]
  (try (let [evaluation (take-while func seq)]
         (if (= (count seq) (count evaluation))
           nil
           (->> evaluation count (nth seq))))
       (catch Exception e nil)))

(defn parents->string
  "helper format function which, when given a list of commit addresses, returns a formatted string"
  [parents]
  (if (= (count parents) 0)
    ""
    (reduce #() "")))

(defn parent-commit-handler
  "function takes care of the case where there is a p-switch"
  ([message tree-addr] (parent-commit-handler message tree-addr []))
  ([message tree-addr parent-commits]
   (let [exists-eval (which-true #(.exists (io/as-file (file-path %))) parent-commits)
         type-eval (which-true #(= (get-object-type %) "commit") parent-commits)
         commits-concat (fn [x] (reduce str "" (map #(str "parent " % "\n") x)))]
     (cond
       (not (nil? exists-eval)) (println (format "Error: no commit object exists at address %s." exists-eval))
       (not (nil? type-eval)) (println (format "Error: an object exists at address %s, but it isn't a commit." type-eval))
       :else (-> (commits-concat parent-commits)
                 (commit-object author_committer tree-addr message)
                 .getBytes
                 wt/write-object
                 println)))))

(defn commit-tree
  "function for handling commit-tree"
  [[tree-addr m-switch message p-switch & parent-commits]]
  (cond
    (not (.isDirectory (io/file ".git"))) (println "Error: could not find database. (Did you run `idiot init`?)")
    (nil? tree-addr) (println "Error: you must specify a tree address.")
    (not (.exists (io/as-file (file-path tree-addr)))) (println "Error: no tree object exists at that address")
    (not= (get-object-type tree-addr) "tree") (println "Error: an object exists at that address, but it isn't a tree.")
    (not= m-switch "-m") (println "Error: you must specify a message.")
    (nil? message) (println "Error: you must specify a message with the -m switch.")
    (= p-switch "-p") (cond
                        (nil? parent-commits) (println "Error: you must specify a commit object with the -p switch")
                        :else (parent-commit-handler message tree-addr parent-commits))
    :else (parent-commit-handler message tree-addr)))

