FROM maven:3.5-jdk-8
COPY /. /app/
WORKDIR /app
RUN mvn package
WORKDIR /app/target
CMD ["java", "-jar", "/app/target/server-1.4a.jar"]