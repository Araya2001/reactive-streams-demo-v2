FROM gradle:jdk21 AS build-env
LABEL authors="aarayaj"
WORKDIR /app

COPY build.gradle ./
COPY src ./src
RUN gradle bootJar -Pvaadin.productionMode=true -Dvaadin.force.production.build=true


FROM openjdk:21-jdk-slim
LABEL authors="aarayaj"

COPY --from=build-env /app/build/libs/*.jar /app.jar
ENV PORT 8080
CMD ["java","-jar","/app.jar"]