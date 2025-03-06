FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/user-0.0.1-SNAPSHOT.jar user.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "user.jar"]
