# Stage 1: build jar
FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# Stage 2: runtime
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/*.jar ./vulnerable-java-demo.jar
RUN mkdir -p /app/data
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/vulnerable-java-demo.jar"]
