FROM gradle:jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11-jre-slim

EXPOSE 8080

RUN mkdir /app

COPY ./environment /app/environment
COPY --from=build /home/gradle/src/build/libs/*.jar /app/environment/agents/
