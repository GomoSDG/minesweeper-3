(ns minesweeper.core)


(defn main []
  (println "Hello World!"))


(defn calculate-orientation [x1 x2 y1 y2]
  (let [num (- y2 y1)
        denum (- x2 x1)
        slope (/ num denum)]
    (cond
      (= denum 0)
      :vertical

      (> slope 0)
      :left

      (< slope 0)
      :right

      (zero? 0)
      :horizontal)))


(defn initialise-grid [width height]
  {:width width
   :height height
   :data (->> (for [x (range width)
                    y (range height)

                    :let [c (str x "," y)
                          edges (->> (for [xe (range (dec x) (+ x 2))
                                           ye (range (dec y) (+ y 2))
                                           :let [ce  (str xe "," ye)]
                                           :when (and (not= c ce) (>= xe 0) (>= ye 0) (< xe width) (< ye height))]
                                       {:cell-id ce
                                        :orientation (calculate-orientation x xe y ye)})
                                     (set))]]
                {:x x
                 :y y
                 :neighbours edges
                 :data nil
                 :id c})
              (map (juxt :id identity))
              (into {}))})


(defn set-cell-data
  ([grid x y data]
   (set-cell-data grid (str x "," y) data))
  ([grid coords data]
   (update-in grid [:data coords] assoc :data data)))


(defn get-cell
  ([grid x y]
   (get-cell grid (str x "," y)))
  ([grid coords]
   (get-in grid [:data coords])))


(defn get-cell-data
  ([grid x y]
   (get-cell-data grid (str x "," y)))
  ([grid coords]
   (-> (get-cell grid coords)
       (:data))))


(defn token-won?
  ([grid orientation token x y]
   (token-won? grid orientation token (str x "," y)))
  ([grid orientation token coords]
   (loop [q #queue [(get-cell grid coords)]
          l 0
          t (set coords)]
     (let [{:keys [neighbours id]} (peek q)
           n' (->> (filter #(and (= (:orientation %) orientation)
                                 (not (t (:cell-id %))))
                           neighbours)
                   (map #(get-cell grid (:cell-id %)))
                   (filter #(= token (:data %))))]
       (if-not (not-empty n')
         (>= l 2)
         (recur (into (pop q) n')
                (inc l)
                (conj t id)))))))


(defn grid-layout [width height]
  (->> (for [y (range width)
             x (range height)]
         (str x "," y))
       (partition-all width)))


(defn print-grid [{:keys [width height] :as grid}]
  (let [layout (grid-layout width height)]
    (doseq [rows layout]
      (doseq [coords rows]
        (print "  |" (get-cell-data grid coords) "|  "))
      (print "\n"))))


(comment
  (-> (initialise-grid 10 10)
      (set-cell-data 1 0 :X)
      (set-cell-data 1 1 :X)
      (set-cell-data 1 2 :X)
      #_(print-grid )
      (token-won? :vertical :X 1 0))

  (grid-layout 3 3)

  (print-grid (initialise-grid 3 3))

  (comment
    [[[0 0] [1 0] [2 0]]
     [[0 1]]])

  (-> (initialise-grid 3 3)
      (set-cell-data  0 0 :X)
      (get-cell-data 0 0)))
