# ==========================================
# STAGE 1: Build the Spring Boot Application
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy pom.xml to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source files
COPY src ./src

# Build the package (skip tests to speed up the process)
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Lightweight Run Environment
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a secure non-root system user and group
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy build artifact from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Application environment configuration
ENV PORT=8080
EXPOSE 8080

# Execute with optimized JVM parameters for containerized environments
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
