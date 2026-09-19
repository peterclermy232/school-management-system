# Build stage: compile the jar with Maven (no need for the mvnw wrapper here since the
# base image already ships a matching Maven version).
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B clean package -DskipTests

# Run stage: just the JRE and the built jar, to keep the final image small.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render (and most PaaS hosts) inject PORT at runtime; application.properties already
# binds to it via server.port=${PORT:8084}.
ENTRYPOINT ["java", "-jar", "app.jar"]
