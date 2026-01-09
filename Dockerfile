it is from web instead mobile focus , but can be run on both.    other information clarification as follows: 1. RabbitMQ  2. synchronous for first MVP 3. use existing open wordlists  4. use playwright  and bdd instead of Detox (or Appium) for e2e tests (Detox preferred for RN). Also before starting do you have any other suggestion to make to more better from QA and dev, devops perspective?# Use Maven to build the project and then run with a slim JRE
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /workspace/target/testpyramid-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

