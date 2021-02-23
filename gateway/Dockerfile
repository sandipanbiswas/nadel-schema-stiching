FROM adoptopenjdk:11-jdk-hotspot as build-env
COPY src/ src
COPY gradle/ gradle
COPY gradlew .
COPY settings.gradle .
COPY build.gradle .
RUN ./gradlew build

FROM adoptopenjdk:11-jre-hotspot
VOLUME /tmp
COPY --from=build-env build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
