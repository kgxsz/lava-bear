(ns styles.util)

(defn translate [x y]
  (str "translate(" x "px," y "px)"))

(defn steps [n]
  (str "steps(" n ")"))

(defn make-modifiers
 "Creates a vector of modifiers, the modifier names will consist of the modifier
  and the constant keys. Any pseudo-class is appended to the modifier and the unit
  function is used on each var values."
  [[modifier properties]]
  (let [[property {:keys [values units pseudo-class]}] (first properties)]
    (for [[k v] values]
      [(keyword (str (name modifier) "-" (name k) pseudo-class))
       {property (if units (units v) v)}])))
