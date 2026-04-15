# Deployment Guide

## Infrastructure Components
- PostgreSQL for `optimize-land-reg`
- MongoDB for `afis-master` and `afis-service`
- Apache Kafka for event-driven async communication
- Keycloak (IAM)

## Configuration
Deployment files are managed via Docker compose in the `deployments/docker` folder, utilizing Shell scripts for orchestration (e.g., `setup-services.sh`, `deploy-all.sh`).

## Pipelines
Github Actions handles the mobile build (see `.github/workflows/mobile_build.yml`).
