# Use the official Maven image to create a build environment
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy the pom.xml and the project source
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean install -DskipTests

# Use a smaller JRE image for the runtime environment
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/platform-0.0.1-SNAPSHOT.jar .

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "platform-0.0.1-SNAPSHOT.jar"]
