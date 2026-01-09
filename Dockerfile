# Multi-stage Dockerfile for building the Spring Boot app and producing a small runtime image
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only the files needed for a Maven build to leverage caching
COPY pom.xml .
COPY src ./src

# Build the application (adjust -DskipTests if you want tests in CI)
RUN mvn -B -DskipTests package

# Runtime image
FROM eclipse-temurin:17-jre

# Copy the application jar from the build stage
ARG JAR_FILE=/workspace/target/testpyramid-1.0-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
