# Simple Dockerfile - expects JAR to be built locally
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy JAR file (build with: mvn clean package)
COPY target/*.jar app.jar

# Expose port
EXPOSE 8082

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
