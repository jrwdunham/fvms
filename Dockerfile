FROM clojure:openjdk-8-lein-alpine

RUN mkdir -p /src/fvms
WORKDIR /src/fvms
ADD project.clj /src/fvms/

RUN lein deps

COPY src/ /src/fvms/src/

# RUN lein repl :headless :host 0.0.0.0 :port 8085

CMD lein repl :headless :host 0.0.0.0 :port 8085 & \
    lein ring server 8084
