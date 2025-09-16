# Etapa de build con Gradle Wrapper
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test -x validateStructure --no-daemon

# Etapa de runtime con JDK 17 (más liviano)
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]