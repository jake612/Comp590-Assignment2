(ns idiot
  (:require [clojure.java.io :as io])
  (:require [hash :as hash]))

(defn help
  "Function to print help messages for a function"
  [function]
  (cond
    (or (nil? function) (= "idiot" function)) (println "idiot: the other stupid content tracker\n\nUsage: idiot <command> [<args>]\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    (or (= "-h" function) (= "--help" function))  (println "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    (= "init" function) (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
    (= "hash-object" function) (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
    :else (println "Error: invalid command")))

(defn init
  "Function to initialize a new database"
  [args]
  (cond
    (or (= "--help" (first args)) (= "-h" (first args))) (help "init")
    (> (count args) 0) (println "Error: init accepts no arguments")
    :enter-main (if (.isDirectory (io/file ".git"))
                  (println "Error: .git directory already exists")
                  (do (io/make-parents ".git/objects/sample.txt")
                      (println "Initialized empty Idiot repository in .git directory")))))

(defn -main
  "Main method for handling CLI"
  [& args]
  (let [num-args (count args)
        command (first args)]
    (cond
      (or (= num-args 0) (= command "-h") (= command "--help")) (help "idiot")
      (= command "help") (help (second args))
      (= command "init") (if (or (= "-h" (second args)) (= "--help" (second args)))
                           (help "hash-object")
                           (hash/hash-object (rest args)))
      (= command "hash-object") (hash/hash-object (rest args))
      :else (println "Error: invalid command"))
    ))

(= "arg" nil)