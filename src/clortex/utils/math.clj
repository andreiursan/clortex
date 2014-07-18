(ns clortex.utils.math)

(defn scientific [x]
  (format "%.3g" (double x)))

(defn text-scientific [x]
  (clojure.string/replace (scientific x) #"e((\+|-)\d+)" "x10^$1"))

(defn latex-scientific [x]
  (clojure.string/replace (scientific x) #"e((\+|-)\d+)" "\\\\times10^{$1}"))

(defn factorial [n] (loop [i n val 1N] (if (= i 1) val (recur (dec i) (* i val)))))
#_(fact (factorial 3) => 6)
(defn binomial [n k] (/ (factorial n) (* (factorial (- n k)) (factorial k))))
#_(fact (binomial 3 2) => 3)
#_(fact (binomial 5 2) => 10)

(defn random-fn-with-seed [n]
	(let [r (java.util.Random. n)]
	  (fn [m] (.nextInt r m))))

(defn abs-diff [x y] (. Math abs (- x y)))



