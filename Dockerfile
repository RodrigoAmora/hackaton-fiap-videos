# Estágio de build
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Estágio final
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /build/target/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
