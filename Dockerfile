# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests && cp target/*.jar app.jar

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="Sumit Jamod <your-email@example.com>"
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
