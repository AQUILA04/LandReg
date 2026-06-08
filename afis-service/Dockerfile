# Build stage
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Copy maven wrapper and pom
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN ./mvnw package -DskipTests -Pprod

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Run as non-root
RUN useradd -m appuser
USER appuser

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
