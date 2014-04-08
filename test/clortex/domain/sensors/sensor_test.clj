(ns clortex.domain.sensors.sensor-test
  (:use midje.sweet)
  (:use clortex.domain.sensors.core))

[[:chapter {:title "Sensors"}]]

"Sensors gather information from the world and deliver it in encoded form to the CLA.

"

[[:section {:title "Numenta OPF Sensor (CSV data)"}]]

"The first sensor in `clortex` reads CSV data which is compatible with Numenta's OPF
(Online Prediction Framework) software."

[[:code "gym,address,timestamp,consumption
string,string,datetime,float
S,,T,
Balgowlah Platinum,Shop 67 197-215 Condamine Street Balgowlah 2093,2010-07-02 00:00:00.0,5.3
Balgowlah Platinum,Shop 67 197-215 Condamine Street Balgowlah 2093,2010-07-02 00:15:00.0,5.5
Balgowlah Platinum,Shop 67 197-215 Condamine Street Balgowlah 2093,2010-07-02 00:30:00.0,5.1
Balgowlah Platinum,Shop 67 197-215 Condamine Street Balgowlah 2093,2010-07-02 00:45:00.0,5.3
" {:title "a very simple scalar encoder" :tag "simple-scalar-encoder"}]]

"The first line lists the field names for the data in the file. These field names are referenced elsewhere
when specifying the field(s) which need to be predicted, or the encoders to use for that field. The second
line describes the type fo each field (in Python terms). The third line is OPF-specific. `S` (referring to
the `gym` field) indicates that this field, when it changes, indicates a new **sequence** of data records.
The `T` (for the `timestamp` field) indicates that this field is to be treated as time-series data. These two
concepts are important in powering the CLA's sequence learning.
"
[[:file {:src "test/clortex/domain/sensors/date_test.clj"}]]

(fact "after loading the hotgym data, it has 87840 items"
      (def hotgym-config {:file "resources/hotgym.csv"
                          :read-n-records :all
                          :fields ["gym" {:type :string
                                          :doc "Name of this Gym"
                                          :encoder {:type :hash-encoder
                                                    :bits 32
                                                    :on 8}
                                          :sequence-flag? true}
                                   "address" {:type :string
                                              :doc "Address of this Gym"
                                              :encoder {:type :hash-encoder
                                                        :bits 32
                                                        :on 8}}
                                   "timestamp" {:type :datetime
                                                :doc "Timestamp of this data record"
                                                :subencode [{:field :day-of-year}
                                                            {:field :day-of-week}
                                                            {:field :time-of-day}
                                                            {:field :weekday?}]}
                                   ]})
      (def hotgym (load-opf-file hotgym-config))
      (count (:parsed-data hotgym)) => 87840)

#_(write-edn-file hotgym "resources/hotgym.edn")
