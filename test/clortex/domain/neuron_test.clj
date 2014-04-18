(ns clortex.domain.neuron-test
  (:use midje.sweet)
  (:require [clortex.domain.neuron :as n]
            [datomic.api :as d]
            [clortex.utils.math :refer :all]
            [clortex.utils.datomic :refer :all]
            [clortex.domain.patch.pure-patch :as purep]
            [clortex.domain.neuron.pure-neuron :as pure-neuron]
            [clortex.domain.patch.persistent-patch :as dbp]
            [adi.core :as adi]))

(fact "create a pure patch"
(def n 1024)
(def p (.set-neurons (purep/patch) (repeat n (pure-neuron/neuron))))
(count (.neurons p)) => n
)

(defn create-in-memory-db []
  (let [uri "datomic:mem://patch-test-db"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (load-file "resources/datomic/schema.edn")]
      (d/transact conn schema)
      conn)))


(defn create-free-db []
  (let [uri "datomic:free://localhost:4334/patches"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (load-file "resources/datomic/schema.edn")]
      (d/transact conn schema)
      conn)))

(defn create-adi-in-memory-db []
  (let [uri "datomic:mem://patch-adi-db"]
    (adi/datastore uri clortex-schema true true)))

(def patch-1 (d/squuid))
(def patch-2 (d/squuid))
(def patch-3 (d/squuid))

(def patch-4 (d/squuid))

(fact "create an adi-based db, add a patch"
      (let [uri "datomic:mem://adi-test"
            ds (adi/datastore uri clortex-schema true true)
            _add  (adi/insert! ds [{:patch {:uuid patch-1}}])
            check (->> (adi/select ds {:patch/uuid patch-1})
                       first :patch :uuid)
            _tidy (d/delete-database uri)]
        check) => patch-1
)


(fact "After creating a patch, we can find the patch"
      (let [ctx {:conn (create-in-memory-db)}
            _ (dbp/create-patch ctx patch-1)
            patch (dbp/load-patch-by-uuid ctx patch-1)]
        (:patch/uuid (:patch patch))) => patch-1
)

(fact "After creating several patches, we can find them all"
      (let [ctx {:conn (create-in-memory-db)}]
        (dbp/create-patch ctx patch-1)
        (dbp/create-patch ctx patch-2)
        (dbp/create-patch ctx patch-3)
        (dbp/find-patch-uuids ctx)) => #{[patch-1] [patch-2] [patch-3]}
)

(fact "Adding a neuron to a patch, we can find the neuron"
      (let [ctx {:conn (create-in-memory-db)}]
        (dbp/create-patch ctx patch-1)
        (dbp/add-neuron ctx patch-1)
        (dbp/find-neurons ctx patch-1)) => #{[0]}
      )

(fact "Adding several neurons to patches, we can find the neurons"
      (let [ctx {:conn (create-in-memory-db)}]
        (dbp/create-patch ctx patch-1)
        (dbp/add-neuron ctx patch-1)
        (dbp/create-patch ctx patch-2)
        (dbp/add-neuron ctx patch-2)
        (dbp/add-neuron ctx patch-1)
        (dbp/add-neuron ctx patch-1)
        (dbp/find-neurons ctx patch-1)) => #{[0] [1] [2]}
      )

(fact "Adding many neurons to a patch, we can find the neurons"
      (def n 65536)
      (def n 1024)
      (let [ctx {:conn (create-in-memory-db)}]
        (dbp/create-patch ctx patch-1)
        (time (dbp/add-neurons-to! ctx patch-1 n))
        (count (dbp/find-neurons ctx patch-1))) => n
      )

(fact "Connecting one neuron to another, we can find the synapse"
      (let [ctx {:conn (create-in-memory-db) :randomer (random-fn-with-seed 123456)}]
        (dbp/create-patch ctx patch-1)
        (dbp/add-neuron ctx patch-1)
        (dbp/add-neuron ctx patch-1)
        (dbp/connect-distal ctx patch-1 0 1 false)
        (count (dbp/synapse-between ctx patch-1 0 1))) => 1
      )

(fact "Connecting many neurons to one another, we can find the synapses"
      (let [ctx {:conn (create-free-db) :randomer (random-fn-with-seed 123456)}
            n 4096
            randomer (random-fn-with-seed 123456)]
        (dbp/create-patch ctx patch-1)
        (dbp/add-neurons-to! ctx patch-1 (* n 2))
        (dbp/connect-distal ctx patch-1 0 1 false)
        (time (doseq [i (range 100)]
          (dbp/connect-distal ctx patch-1 (randomer n) (+ n (randomer n)) false)))
        (count (dbp/synapse-between ctx patch-1 0 1))) => 1
      )

#_(fact "Adding inputs to a patch, we can write and read the SDR"
      (let [ds (create-adi-in-memory-db)
            ctx {:ds ds :conn (:conn ds) :randomer (random-fn-with-seed 123456)}]
        (dbp/create-adi-patch ctx patch-1)
        (dbp/add-inputs-to! ctx patch-1 12)
        (dbp/input-sdr ctx patch-1) => [0 0 0 0 0 0 0 0 0 0 0 0]
      ))



