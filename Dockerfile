FROM alpine:3.7
RUN apk add --no-cache maven openjdk8
COPY /. /app/
WORKDIR /app
RUN mvn package \
    && cp Server/src/main/resources/config.properties /app/
CMD ["java", "-jar", "/app/target/server-1.2a.jar"]
