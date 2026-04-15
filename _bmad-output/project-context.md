---
project_name: 'LandReg'
user_name: 'Francis'
date: '2026-03-31T23:58:00+04:00'
sections_completed: ['technology_stack', 'language_rules', 'framework_rules', 'testing_rules', 'quality_rules', 'workflow_rules', 'anti_patterns']
status: 'complete'
rule_count: 16
optimized_for_llm: true
---

# Project Context for AI Agents

_This file contains critical rules and patterns that AI agents must follow when implementing code in this project. Focus on unobvious details that agents might otherwise miss._

---

## Technology Stack & Versions

- **Backend:** Java 17, Spring Boot 3.4.1
- **Database:** PostgreSQL (with Flyway), H2 for tests
- **Messaging:** Apache Kafka (`spring-kafka`)
- **Key Java Libs:** Lombok (1.18.32), MapStruct (1.6.0.Beta1), Spring Cloud OpenFeign (4.2.0)
- **Frontend:** Angular 18.2.9 structured with Module Federation
- **Frontend UI/Styling:** Bootstrap 5.3.3, FontAwesome
- **Custom Modules:** `common-entities` and `common-securities` libraries are central to the ecosystem

## Critical Implementation Rules

### Language-Specific Rules

- **Java Boilerplate:** Always prioritize Lombok (`@Data`, `@Slf4j`, `@NoArgsConstructor`, etc.) over manual boilerplate.
- **Java Bean Mapping:** Strictly map between Entities and DTOs using MapStruct interfaces.
- **TypeScript Temporary ID Generation:** When generating new records offline, always assign a string UUID generator for the primary key. This avoids clashes with the numeric IDs assigned by the server upon synchronization.

### Framework-Specific Rules

- **Angular Components:** When modifying or creating an Angular component, you must not remove or omit the `standalone: false` property from the `@Component` decorator to preserve compatibility with the NgModule architecture.
- **Spring API Responses:** The project enforces a strict API response wrapper. All controllers must return data wrapped like this: `Response.builder().status(HttpStatus.OK).statusCode(HttpStatus.OK.value()).message("default.message.success").service("OPTIMIZE-SERVICE").data(data).build()`.
- **Spring Controllers:** New REST controllers must extend `com.optimize.common.entities.controller.BaseController<Entity, Type>` to inherit standard `getAll`, `getOne`, `register`, and `deleteSoft` functionality.

### Testing Rules

- **Coverage Enforcement (Backend):** All new Java code must be sufficiently tested to maintain the Jacoco instruction coverage baseline of 85% (`0.85`).
- **Naming Conventions:** Strict enforcement of backend test naming conventions where unit tests are suffixed with `Test.java` and integration tests with `ITest.java` to ensure the Maven Surefire and Failsafe plugins execute them properly.
- **Mock & Integration Boundaries:** Backend repository/integration tests must rely on the configured H2 test database and corresponding test profiles rather than mocking the JPA context, while strictly keeping PostgreSQL for runtime environments.

### Code Quality & Style Rules

- **Prettier & ESLint Enforcement:** All frontend code must strictly conform to the existing Prettier formatting rules and ESLint configuration. Ensure code is compliant before concluding frontend tasks.
- **Java Checkstyle & SonarQube:** Backend Java code must pass the configured Checkstyle verifications. Do not ignore checkstyle warnings and address SonarQube-like bad practices (e.g., unused imports, empty blocks) immediately. 
- **Naming Conventions:** Maintain strict suffix naming (e.g., `*Dto.java` for Data Transfer Objects, `*Controller.java` for API endpoints, `*Service.java` for business logic).

### Development Workflow Rules

- **Containerization via Jib:** Docker images for the backend are built automatically using the Jib Maven Plugin. Do not introduce custom `Dockerfile` artifacts for the Spring Boot application unless specifically required for native images that Jib cannot handle.
- **Spring & Angular Profiles:** Rely strictly on the existing configured application profiles (`dev`, `prod`, `e2e`) when deploying, and always use the existing scripts to trigger backend builds to ensure profiles apply correctly.
- **Local Services Sandbox:** Local backend services and dependencies (e.g., PostgreSQL, Kafka, Keycloak, Consul) must be launched and managed via their respective `docker-compose.yml` configurations located in the docker directories.

### Critical Don't-Miss Rules

- **Offline ID Generation Conflict:** When scaffolding UI forms that allow the creation of entities before syncing, NEVER use numeric placeholder IDs. You MUST use UUID strings as temporary primary keys until the backend assigns real database IDs.
- **DTO Mapping Rigidity:** All data transformations between Entities and DTOs MUST be handled strictly by MapStruct `@Mapper` interfaces. Never write manual transformation code.
- **Boilerplate Evasion:** Never write manual accessor or mutator functions in Java domains. Always rely on Lombok's annotations.
- **Security Library Coupling:** Ensure all new controllers and services leverage the `common-securities` dependency to align and authorize authentication tokens dynamically.

---

## Usage Guidelines

**For AI Agents:**

- Read this file before implementing any code
- Follow ALL rules exactly as documented
- When in doubt, prefer the more restrictive option
- Update this file if new patterns emerge

**For Humans:**

- Keep this file lean and focused on agent needs
- Update when technology stack changes
- Review quarterly for outdated rules
- Remove rules that become obvious over time

Last Updated: 2026-04-01T00:15:00+04:00
