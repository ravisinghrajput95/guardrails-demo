# ---- Stage 1: Build with Maven (JDK 8) ----
FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
    
# Build the jar (no tests for speed)
RUN mvn -DskipTests package
    
# ---- Stage 2: Runtime (JRE 8) ----
FROM eclipse-temurin:8-jre
    
WORKDIR /app
    
# Copy the built jar
COPY --from=build /app/target/vulnerable-java-demo-1.0.0.jar /app/
    
# Ensure the directory for the SQLite DB exists (your app expects data/demo.db)
RUN mkdir -p /app/data
    
EXPOSE 8080
CMD ["java","-jar","/app/vulnerable-java-demo-1.0.0.jar"]
    