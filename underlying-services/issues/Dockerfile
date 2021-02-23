FROM openjdk:8-jdk-alpine as build-env
COPY src/ src
COPY gradle/ gradle
COPY gradlew .
COPY settings.gradle .
COPY build.gradle .
RUN ./gradlew build
VOLUME /tmp

FROM openjdk:8-jre-alpine
COPY --from=build-env build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
