FROM openjdk:17-jdk-slim as builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM openjdk:17-jdk-slim
WORKDIR /app

RUN mkdir jars
COPY --from=builder /app/build/libs/*.jar jars/

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/jars/fault-injector-0.0.1-SNAPSHOT.jar"]
