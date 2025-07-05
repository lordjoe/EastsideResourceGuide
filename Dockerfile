# Use a minimal JDK base image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the Spring Boot JAR
COPY target/EastsideResourceGuide-1.0-SNAPSHOT.jar app.jar

# add firebase secret
COPY src/main/resources/firebase-secret.json /app/firebase-secret.json

# Expose the Spring Boot default port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]