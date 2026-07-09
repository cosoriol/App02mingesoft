# Etapa de construccion
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY package-service/pom.xml package-service/pom.xml
COPY package-service/src package-service/src
RUN mvn -f package-service/pom.xml clean package -DskipTests

# Etapa de ejecucion
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/package-service/target/package-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
