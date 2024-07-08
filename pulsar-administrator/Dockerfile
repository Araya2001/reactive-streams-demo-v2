FROM gradle:jdk21 AS build-env
WORKDIR /app
COPY build.gradle ./
COPY src ./src
RUN gradle bootJar
FROM openjdk:21
COPY --from=build-env /app/build/libs/*.jar /app.jar
CMD ["java","-jar","/app.jar"]