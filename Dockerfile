FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/chrms-backend-1.0.0.jar app.jar

RUN mkdir -p /app/uploads /app/exports

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
