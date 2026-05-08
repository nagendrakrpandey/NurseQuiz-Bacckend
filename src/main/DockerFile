# Use Maven + JDK for building the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy all files
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn -e -X -DskipTests clean package

# ============================
# Runtime Image
# ============================
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Render port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]