# Development Guide

## Prerequisites
- JDK 17 for backend modules (`afis-master`, `afis-service`, `optimize-land-reg`)
- Node.js 22+ for Angular frontend (`afis-master` webapp)
- Android Studio / Android SDK for `mobile`
- Docker and Docker Compose

## Build Commands
- **Backend**: `./mvnw clean install` in respective folders.
- **Frontend**: `npm install` and `npm start` in `afis-master`.
- **Mobile**: `./gradlew build` in `mobile`.

## Environment Setup
Run `docker compose` to bring up databases, Kafka, and Keycloak for local development. See `deployments/` folder.
