# Etapa de construccion
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY payment-service/pom.xml payment-service/pom.xml
COPY payment-service/src payment-service/src
RUN mvn -f payment-service/pom.xml clean package -DskipTests

# Etapa de ejecucion
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/payment-service/target/payment-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
