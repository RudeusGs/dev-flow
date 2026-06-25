# Stage 1: Build the Spring Boot Application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build

# Copy only the POM and download dependencies first (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code and build the JAR
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Create the runtime environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add a volume for Git repositories
VOLUME /data/git-repos

# Create a non-root user for security (optional but recommended)
RUN addgroup -S spring && adduser -S spring -G spring
# Commented out to prevent volume write permission issues on /data/git-repos
# USER spring:spring

# Copy the built JAR from the previous stage
COPY --from=build /build/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
