# Data Models: Optimize Land Reg

## Database Schema Overview
The `optimize-land-reg` module uses PostgreSQL (`@Entity`) with Spring Data JPA.

## Entites
- **Actor** / **AbstractActor**: Represents individuals or organizations.
- **Person**, **PublicLegalEntity**, **PrivateLegalEntity**, **InformalGroup**: Types of actors.
- **Bordering**: Boundary properties or relationships.
- **CheckListOperation**: Validation checklists.
- **Conflict**: Disputes over land.
- **Finding**: Observations related to land.
- **FingerprintMatchingHistory**, **FingerprintStore**: Fingerprint tracking.
- **Registration**: Core land registration element.
- **SynchroHistory**: Synchronization status.
- **OutboxEvent**: Outbox pattern for events.
- **IdentificationDoc**: Identity documents for actors.
