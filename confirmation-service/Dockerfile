# Etapa de construccion
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY confirmation-service/pom.xml confirmation-service/pom.xml
COPY confirmation-service/src confirmation-service/src
RUN mvn -f confirmation-service/pom.xml clean package -DskipTests

# Etapa de ejecucion
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/confirmation-service/target/confirmation-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
