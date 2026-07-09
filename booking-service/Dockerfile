# Etapa de construccion
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY booking-service/pom.xml booking-service/pom.xml
COPY booking-service/src booking-service/src
RUN mvn -f booking-service/pom.xml clean package -DskipTests

# Etapa de ejecucion
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/booking-service/target/booking-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
