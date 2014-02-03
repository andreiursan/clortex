# clortex

Clortex is an implementation in Clojure of Jeff Hawkins' Hierarchical Temporal Memory & Cortical Learning Algorithm. See the [docs](https://github.com/fergalbyrne/clortex/doc/index.html) for full details.

## Example Usage

(require '[clortex.core])

(make-cortex test-cortex)

(add-encoder test-cortex (file-encoder "data.csv"))

(run-cortex test-cortex :all-records)

## Developer Information

* [Documentation](https://github.com/fergalbyrne/clortex/doc/index.html)
* [GitHub project](https://github.com/fergalbyrne/clortex)

## License

Copyright © 2014 Fergal Byrne, Brenter IT

Distributed under the Apache Public License either version 2.0 or (at
your option) any later version.
