# LandReg

LandReg is a decentralized land registration and actor management platform. It allows field agents to capture information offline via mobile, synchronizing it securely to a centralized backend with fingerprint identification mechanisms.

## Project Structure

This is a multi-part project consisting of the following modules:

- **`afis-master/`**: Frontend & Backend for managing the Automated Fingerprint Identification System (Spring Boot + Angular).
- **`afis-service/`**: Microservice handling intensive match jobs and interactions with the fingerprint store (Spring Boot).
- **`mobile/`**: Android application used on the field by agents.
- **`optimize-land-reg/`**: Core business entity API for the Land Registration domain (Spring Boot + PostgreSQL).
- **`deployments/`**: Infrastructure configuration and Docker Compose environment setup.

## Documentation

Comprehensive documentation for this project has been automatically generated and is stored in the `docs/` folder. 

👉 **Start here: [Project Documentation Index](docs/index.md)**

### Key Documents:
- [Project Overview](docs/project-overview.md)
- [Development Guide](docs/development-guide.md)
- [Deployment Guide](docs/deployment-guide.md)
- [Integration Architecture](docs/integration-architecture.md)
- [Source Tree Analysis](docs/source-tree-analysis.md)

## Getting Started

To set up the development environment, please refer to the [Development Guide](docs/development-guide.md). 
For deployment and infrastructure instructions using Docker, see the [Deployment Guide](docs/deployment-guide.md).
