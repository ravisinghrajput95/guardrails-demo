# Stage 1: Build with JDK
FROM openjdk:8-jdk-alpine AS build

WORKDIR /app
COPY pom.xml /app/
COPY src /app/src

RUN apk add --no-cache maven && \
    mvn -f /app/pom.xml -DskipTests package


# Stage 2: Run with the old JRE for SCA demo
FROM openjdk:8-jre-alpine

WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/vulnerable-java-demo-1.0.0.jar /app/

# Ensure the directory for the SQLite DB exists
RUN mkdir -p /app/data

EXPOSE 8080
CMD ["java","-jar","/app/vulnerable-java-demo-1.0.0.jar"]
