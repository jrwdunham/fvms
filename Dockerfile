FROM clojure:openjdk-8-lein-alpine

RUN mkdir -p /src/fvms
WORKDIR /src/fvms
ADD project.clj /src/fvms/

RUN lein deps

CMD lein ring server
