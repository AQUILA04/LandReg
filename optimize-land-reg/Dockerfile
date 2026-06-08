# Build stage
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Copy the shared libraries first to build them
COPY ../lib /lib
RUN cd /lib/common-entities && ./mvnw clean install -DskipTests || (apt-get update && apt-get install -y maven && mvn clean install -DskipTests)
RUN cd /lib/common-securities && ./mvnw clean install -DskipTests || mvn clean install -DskipTests

# Copy maven wrapper and pom
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN ./mvnw package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Run as non-root
RUN useradd -m appuser
USER appuser

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
