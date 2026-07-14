# Etapa de construccion
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY user-service/src user-service/src
RUN mvn -f user-service/pom.xml clean package -DskipTests

# Etapa de ejecucion
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/user-service/target/user-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
