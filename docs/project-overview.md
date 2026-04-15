# Project Overview

## Executive Summary
LandReg is a decentralized land registration and actor management platform. It allows field agents to capture information offline via mobile, synchronizing it securely to a centralized backend with fingerprint identification mechanisms.

## Project Structure
- **afis-master**: Frontend/Backend for managing the Automated Fingerprint Identification System.
- **afis-service**: Microservice handling intensive match jobs and interactions with the fingerprint store.
- **mobile**: Android application used on the field by agents.
- **optimize-land-reg**: Core business entity API for the Land Registration domain.
- **deployments**: Infrastructure configuration.

## Tech Stack
- Frontend: Angular
- Mobile: Android Native
- Backend: Java Spring Boot
- State/DBs: PostgreSQL, MongoDB
- Async/Events: Kafka
