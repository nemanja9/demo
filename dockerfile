# Using a base image with Java
FROM openjdk:21-jdk-slim

# Setting the working directory
WORKDIR /app

# Copying the built JAR file (located in build/libs/ for Gradle)
COPY build/libs/*.jar app.jar

# Exposing the app's port
EXPOSE 8080

# Running the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]