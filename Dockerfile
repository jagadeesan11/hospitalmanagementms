# Multi-stage build for optimized image size
FROM maven:3.9.4-eclipse-temurin-19 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:19-jre-alpine

# Install necessary packages and create non-root user
RUN apk add --no-cache \
    curl \
    && addgroup -g 1001 -S hospital \
    && adduser -S hospital -u 1001 -G hospital

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/hospitalmanagementms-*.jar app.jar

# Create logs directory
RUN mkdir -p logs && chown -R hospital:hospital /app

# Switch to non-root user
USER hospital

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
