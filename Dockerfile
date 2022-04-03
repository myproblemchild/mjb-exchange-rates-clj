FROM clojure as builder
WORKDIR /usr/src/app
COPY . .
RUN lein uberjar

FROM openjdk
WORKDIR /app
COPY --from=builder /usr/src/app/target/uberjar/google-sheets-0.1.0-SNAPSHOT-standalone.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
