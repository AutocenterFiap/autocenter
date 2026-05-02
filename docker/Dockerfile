FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mkdir -p src/main/java src/main/resources src/test/java

RUN mvn dependency:go-offline -B

COPY src ./src
COPY db ./db

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

COPY --from=build /app/db ./db

EXPOSE 8097

ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=prod", \
    "-jar", \
    "app.jar"]

