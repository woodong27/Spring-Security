FROM gradle:jdk-21-and-22 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

COPY . /app

RUN gradle clean build -x test --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/security-jwt.jar

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "security-jwt.jar"]