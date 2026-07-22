# Build from repository root:
#   docker build -f optimize-land-reg/Dockerfile -t landreg-backend-api .

FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY lib/common-entities/pom.xml lib/common-entities/
COPY lib/common-entities/src lib/common-entities/src
RUN mvn -f lib/common-entities/pom.xml clean install -DskipTests -B

COPY lib/common-securities/pom.xml lib/common-securities/
COPY lib/common-securities/src lib/common-securities/src
RUN mvn -f lib/common-securities/pom.xml clean install -DskipTests -B

COPY lib/common-blob-storage/pom.xml lib/common-blob-storage/
COPY lib/common-blob-storage/src lib/common-blob-storage/src
RUN mvn -f lib/common-blob-storage/pom.xml clean install -DskipTests -B

COPY optimize-land-reg/pom.xml optimize-land-reg/
COPY optimize-land-reg/src optimize-land-reg/src
RUN mvn -f optimize-land-reg/pom.xml clean package -DskipTests -B \
    && JAR="$(ls optimize-land-reg/target/optimize-land-reg-*.jar | grep -v plain | head -n1)" \
    && cp "$JAR" /workspace/app.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ENV SPRING_DOCKER_COMPOSE_ENABLED=false
COPY --from=builder /workspace/app.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
