(ns commit-tree
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def author_committer "Linus Torvalds <torvalds@transmeta.com> 1581997446 -0500")

(defn create-commit-tree
  "Once all of the error tests are passed, creates the commit and writes it"
  [args])

(defn check-type
  "checks type of object"
  [file-path]
  (first (str/split (slurp file-path) #" ")))

(defn commit-tree
  "function for handling commit-tree"
  [args]
  (let [file-path #(str ".git/objects/" (subs % 0 2) "/" (subs % 2))]
    (cond
      (not (.isDirectory (io/file ".git"))) (println "Error: could not find database. (Did you run `idiot init`?)")
      (nil? (second args)) (println "Error: you must specify a tree address.")
      (not (.exists (io/as-file (file-path (second args))))) (println "Error: no tree object exists at that address")
      (not= (check-type (file-path (second args))) "040000") (println "Error: an object exists at that address, but it isn't a tree.")
      (not= (nth args 3) "-m") (println "Error: you must specify a message.")
      (nil? (nth args 4)) (println "Error: you must specify a message with the -m switch.")

      (nil? (nth args 5)) (println "Error: you must specify a commit object with the -p switch.")
      :else (create-commit-tree args)
      ))
  )