# Lava Bear

##### A repository to mess around with Component, Untangled, Om/Next, and other goods.
[![Build Status](https://travis-ci.org/kgxsz/lava-bear.svg?branch=master)](https://travis-ci.org/kgxsz/lava-bear)

## Local development setup
- To start the back end: `lein repl :headless`.
  - Then connect to the repl on port `4000`
  - Then start the system: `(start)`
- To start the front end: `lein figwheel`.
  - Then connect to the repl on port `5000`
  - Then start the cljs repl: `(figwheel-sidecar.repl-api/cljs-repl)`
- To compile the css on file changes: `lein garden auto`.
- To see the front end, hit `localhost:3000`.
- To build a the standalone jar: `lein uberjar`.
- To run the standalone jar: `java -jar target/lava-bear-standalone.jar`.
