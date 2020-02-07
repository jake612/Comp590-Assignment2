(ns idiot)

(defn help
  "Function to print help messages for a function"
  [function]
  (cond
    (or (nil? function) (= "idiot" function)) (print "idiot: the other stupid content tracker\n\nUsage: idiot <command> [<args>]\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    (or (= "-h" function) (= "--help" function))  (print "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
    :else (print "Error: invalid command\n")))

(defn -main
  "Main method for handling CLI"
  [& args]
  (let [num-args (count args)
        command (first args)]
    (cond
      (or (= num-args 0) (= command "-h") (= command "--help")) (help "idiot")
      (= command "help") (help (second args))
      :else (print "Error: invalid command\n"))
    ))