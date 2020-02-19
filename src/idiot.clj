(ns idiot
  (:require [clojure.java.io :as io])
  (:require hash)
  (:require cat)
  (:require init)
  (:require help))

(defn -main
  "Main method for handling CLI"
  [& args]
  (let [num-args (count args)
        command (first args)
        check-first (fn [func] (if (or (= "-h" (second args)) (= "--help" (second args)))
                                 (help/help command)
                                 (func (rest args))))]
    (cond
      (or (= num-args 0) (= command "-h") (= command "--help")) (help/help "idiot")
      (= command "help") (help/help (second args))
      (= command "init") (check-first init/init)
      (= command "hash-object") (check-first hash/hash-object)
      (= command "cat-file") (check-first cat/cat-file)
      :else (println "Error: invalid command"))))
