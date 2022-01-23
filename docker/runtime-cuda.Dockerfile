FROM gradle:jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM nvidia/cuda:11.1.1-runtime-ubuntu18.04
RUN apt update && apt install -y --no-install-recommends openjdk-11-jdk
RUN mkdir /app
COPY ./environment /app/environment
COPY --from=build /home/gradle/src/build/libs/*.jar /app/environment/agents/
