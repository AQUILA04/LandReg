# Build from repository root:
#   docker build -f afis-service/Dockerfile -t landreg-afis-service .

FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY lib/common-blob-storage/pom.xml lib/common-blob-storage/
COPY lib/common-blob-storage/src lib/common-blob-storage/src
RUN mvn -f lib/common-blob-storage/pom.xml clean install -DskipTests -B

COPY afis-service/ afis-service/
RUN chmod +x afis-service/mvnw \
    && cd afis-service \
    && ./mvnw -Pprod clean package -DskipTests -B \
    && JAR="$(ls target/afis-service-*.jar | grep -v plain | head -n1)" \
    && cp "$JAR" /workspace/app.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/app.jar /app/app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
