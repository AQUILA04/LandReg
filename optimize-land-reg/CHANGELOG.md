# Changelog

All notable changes to this project will be documented in this file.

## [0.3.0] - 2026-03-13

### Added
- Added `updateFinding` method in `FindingService` to handle finding updates with synchronization support.
- Exposed `PUT /land-reg/api/v1/constatations` endpoint in `FindingController`.
- Added unit tests for `FindingService` (`register` and `updateFinding` methods), covering success and exception scenarios, including `Conflict` and `Bordering` data.
- Added integration tests for `FindingController` covering create and update scenarios with and without conflicts.
- Added integration tests for `ActorController` covering creation of all actor types: `PhysicalPerson`, `InformalGroup`, `PrivateLegalEntity`, and `PublicLegalEntity`.
- Added scenarios for `ActorController` tests with and without identification documents.

### Fixed
- Fixed Kafka port conflict in integration tests by using distinct ports and `@DirtiesContext`.
- Fixed validation errors in `ActorController` integration tests by using valid dummy Base64 images and ensuring all mandatory fields are populated.
- Fixed DTO validation in tests to comply with `@NotNull` and `@Base64Image` constraints.
