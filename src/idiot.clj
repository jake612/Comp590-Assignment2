(ns idiot
  (:require [clojure.java.io :as io]))

(defn help
  "Function to print help messages for a function"
  [function]
  (cond
    (or (nil? function) (= "idiot" function)) (print "idiot: the other stupid content tracker\n\nUsage: idiot <command> [<args>]\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    (or (= "-h" function) (= "--help" function))  (print "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    (= "init" function) (print "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
    :else (print "Error: invalid command\n")))

(defn init
  "Function to initialize a new database"
  [args]
  (cond
    (or (= "--help" (first args)) (= "-h" (first args))) (help "init")
    (> (count args) 0) (print "Error: init accepts no arguments\n")
    :enter-main (if (.isDirectory (io/file ".git"))
                  (print "Error: .git directory already exists\n")
                  (do (io/make-parents ".git/objects/sample.txt")
                      (print "Initialized empty Idiot repository in .git directory\n")))))

(defn -main
  "Main method for handling CLI"
  [& args]
  (let [num-args (count args)
        command (first args)]
    (cond
      (or (= num-args 0) (= command "-h") (= command "--help")) (help "idiot")
      (= command "help") (help (second args))
      (= command "init") (init (rest args))
      :else (print "Error: invalid command\n"))
    ))

(= "arg" nil)