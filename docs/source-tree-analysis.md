# Source Tree Analysis

## Overall Structure
```
project-root/
├── afis-master/         # AFIS Master Frontend/Backend (Part: afis-master)
│   ├── src/main/java    # Java Spring Boot Backend
│   └── src/main/webapp  # Angular Frontend
├── afis-service/        # AFIS Service (Part: afis-service)
│   └── src/main/java    # Java Spring Boot Microservice
├── mobile/              # Mobile Application (Part: mobile)
│   └── app/src/main     # Android Kotlin/Java source
├── optimize-land-reg/   # Optimize Land Registration API (Part: optimize-land-reg)
│   └── src/main/java    # Java Spring Boot API
└── deployments/         # Deployments & Infrastructure (Part: deployments)
    └── docker/          # Docker compose files
```

## Integration Points
- **mobile** connects to **optimize-land-reg** APIs for synchronizing data regarding actors, registrations, and findings.
- **optimize-land-reg** communicates with **afis-service** (or afis-master) for fingerprint matching validation via Kafka/APIs.
- **afis-master** provides a management interface for the AFIS system and likely shares database or Kafka topic events with **afis-service**.
