# Build stage
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
RUN mkdir -p /uploads /outputs
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
