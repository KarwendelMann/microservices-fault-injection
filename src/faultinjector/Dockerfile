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

# Set the entry point to run the JAR
ENTRYPOINT ["java","-jar","/app/jars/fault-injector-0.0.1-SNAPSHOT.jar"]
