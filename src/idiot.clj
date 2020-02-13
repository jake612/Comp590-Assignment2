(ns idiot
  (:require [clojure.java.io :as io])
  (:require hash)
  (:require cat))

(defn help
  "Function to print help messages for a function"
  [function]
  (cond
    (or (nil? function) (= "idiot" function)) (println "idiot: the other stupid content tracker\n\nUsage: idiot <command> [<args>]\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    (or (= "-h" function) (= "--help" function) (= "help" function))  (println "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    (= "init" function) (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
    (= "hash-object" function) (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
    (= "cat-file" function) (println "idiot cat-file: print information about an object\n\nUsage: idiot cat-file -p <address>\n\nArguments:\n   -h          print this message\n   -p          pretty-print contents based on object type\n   <address>   the SHA1-based address of the object")
    :else (println "Error: invalid command")))

(defn init
  "Function to initialize a new database"
  [args]
  (cond
    (or (= "--help" (first args)) (= "-h" (first args))) (help "init")
    (> (count args) 0) (println "Error: init accepts no arguments")
    :else (if (.isDirectory (io/file ".git"))
                  (println "Error: .git directory already exists")
                  (do (io/make-parents ".git/objects/sample.txt")
                      (println "Initialized empty Idiot repository in .git directory")))))

(defn -main
  "Main method for handling CLI"
  [& args]
  (let [num-args (count args)
        command (first args)
        check-first (fn [func] (if (or (= "-h" (second args)) (= "--help" (second args)))
                                 (help command)
                                 (func (rest args))))]
    (cond
      (or (= num-args 0) (= command "-h") (= command "--help")) (help "idiot")
      (= command "help") (help (second args))
      (= command "init") (check-first init)
      (= command "hash-object") (check-first hash/hash-object)
      (= command "cat-file") (check-first cat/cat-file)
      :else (println "Error: invalid command"))))
