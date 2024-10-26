# Build the JAR file
FROM gradle:8.10.2-jdk17 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle build --no-daemon

# Start the container
FROM --platform=linux/amd64 openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/LinkSqueeze-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
