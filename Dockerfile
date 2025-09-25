#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#COPY target/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

FROM maven:3.9-eclipse-temurin-17

# Copia o arquivo JAR do seu projeto para dentro do container
COPY ./target/fiap-videos-0.0.1-SNAPSHOT.jar  /app/app.jar

# Define o diretório de trabalho
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw
# Faça o download das dependencias do pom.xml
RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)



# Define o comando de inicialização do seu projeto
CMD java -jar app.jar