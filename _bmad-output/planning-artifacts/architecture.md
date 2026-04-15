---
stepsCompleted: ['step-01-init', 'step-02-context', 'step-03-starter', 'step-04-decisions', 'step-05-patterns', 'step-06-structure', 'step-07-validation', 'step-08-complete']
inputDocuments: ['c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\planning-artifacts\prd.md', 'c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\project-context.md']
workflowType: 'architecture'
project_name: 'LandReg Admin Portal'
user_name: 'Francis'
date: '2026-04-09T15:19:00+04:00'
status: 'complete'
completedAt: '2026-04-09T15:47:00+04:00'
---

# Architecture Decision Document

_This document builds collaboratively through step-by-step discovery. Sections are appended as we work through each architectural decision together._

## Project Context Analysis

### Requirements Overview

**Functional Requirements:**
- **Identity & RBAC (FR1-FR5):** Native, strict integration with existing `common-securities` backend for authentication, generating isolated user profiles, and filtering action privileges on the UI (e.g., hiding Export buttons dynamically).
- **Data Grids & Cascades (FR6-FR14):** Complex server-side paginated tables representing `Actors`, `Findings`, and `SynchroHistory`. Requires stateful, responsive geographic cascade filters (Region -> Prefecture -> Commune -> Canton) mapped with chronological ranges.
- **Asynchronous Mass Export (FR15-FR21):** Triggering extensive CSV/XLSX generation. Requires HTTP short-polling architecture on the frontend to display an active processing state, ultimately presenting a non-blocking download vector notification, alongside automatic UI audit records.

**Non-Functional Requirements:**
- **Performance:** 500ms guaranteed render times for cascade page transitions. Lightweight payload execution on short-polling loops.
- **Security:** Strict JWT lifecycle management. Redirection patterns for expired access tokens.
- **Usability:** Premium aesthetic from scratch. Smooth micro-animations, absolute data clarity, no generic browser defaults. 
- **Scalability:** The frontend must never hold total datasets in memory; infinite scroll or chunked pagination must be optimized at the component level.

**Scale & Complexity:**
- The strict requirement for async polling states, nested mapping filters, and absolute reliance on the `common-securities` module elevate this beyond a simple CRUD interface.
- Primary domain: Web Frontend (Standalone Angular 18 Admin Portal)
- Complexity level: Medium-High (Due to non-blocking async UX states and nested dynamic routing/RBAC views).
- Estimated architectural components: At least 4 core modules (Auth/RBAC, Data Grids, Async Notification Poller, Shared State Managers).

### Technical Constraints & Dependencies

- **Framework:** Angular 18 in a standalone host paradigm (not a legacy micro-frontend remote).
- **Styling:** Vanilla CSS strictly enforced; obsolete classes or ad-hoc frameworks like Tailwind are expressly prohibited.
- **Dependency:** Tight-coupling with the JVM Spring Boot REST APIs (`optimize-land-reg`) and the `common-securities` auth library.
- **UX Lack:** No UX designs exist, meaning architectural structure must define extremely rigorous modular boundaries for styling to prevent CSS sprawl.

### Cross-Cutting Concerns Identified

- **Global State Management:** The active geographic cascade selection must persist effectively when navigating between different grid domains.
- **Background Task Polling:** A global service singleton must orchestrate HTTP short-polling transparently and project Toast notifications regardless of the active router outlet.
- **Auth Guarding & HTTP Interceptors:** A unified mechanism for intercepting HTTP 401s, refreshing JWTs, and mapping the `EXPORT_DATA` token permissions directly to DOM element visibility natively utilizing the `ngx-permissions` library (e.g., `*ngxPermissionsOnly`).

## Starter Template Evaluation

### Primary Technology Domain

Web Frontend (Standalone Angular 18 Admin Portal) based on strict project context requirements.

### Starter Options Considered

Since the project context explicitly designates Angular 18 without alternative rendering frameworks, selecting differing web ecosystems (e.g., Next.js or SvelteKit) is excluded. Our core decision revolves purely around the cleanest CLI scaffolding methodology for v18.

### Selected Starter: Native Angular CLI (Standalone-First)

**Rationale for Selection:**
Utilizing the official `@angular/cli@latest` pipeline guarantees perfect alignment with the Angular team's long-term support model. By explicitly leveraging the v18 native Standalone architecture (injecting `provideHttpClient`, `provideRouter` via `app.config.ts`), we eliminate archaic `NgModule` boilerplate entirely. This reduces memory footprint and simplifies lazy-loading our complex data-grid and geographical cascading filter components.

**Initialization Command:**

```bash
npx @angular/cli@latest new manager-portal --style=css --routing=true --skip-tests=false --ssr=false
```

**Architectural Decisions Provided by Starter:**

**Language & Runtime:**
TypeScript pre-configured with strict-mode compilation targeting modern ESNext browser standards. 

**Styling Solution:**
Vanilla CSS (`--style=css`). Perfectly adheres to the project rules forbidding Tailwind/Bootstrap dependency layers. All aesthetic requirements will be handled natively via SCSS/CSS variables.

**Build Tooling:**
Angular's new ESBuild/Vite-powered system for rapid Hot Module Replacement (HMR) and optimized build bundles, crucial for 500ms guaranteed transition times (NFR-PERF-1).

**Testing Framework:**
Default testing suite scaffolding (Jasmine/Karma) natively integrated, ensuring strict testability guidelines for complex frontend state matrices.

**Code Organization:**
Absolute `standalone: true` configurations. Directives (like the chosen `*ngxPermissionsOnly` from our Context step) and Pipes can be imported component-by-component exactly where needed.

**Development Experience:**
Strongly typed templates, instantaneous development rebuilds, and standard CLI generation tools (`ng g c ...`) that any modern Angular developer understands instantly.

**Note:** Project initialization using this command should be the first implementation story.

## Core Architectural Decisions

### Decision Priority Analysis

**Critical Decisions (Block Implementation):**
- **Starter CLI & Ecosystem:** Native Angular 18 Standalone API via `@angular/cli@latest`.
- **Authorization Enforcement:** The `ngx-permissions` package directly intercepts structural rendering against JWT claims.

**Important Decisions (Shape Architecture):**
- **State Management:** `@ngrx/signals` (v21.1.0) provides surgical, boilerplate-free reactivity for the Geographic Cascade and Async notification states.
- **UI Architecture:** 100% custom-built DOM trees for the heavy data-grids, enforcing Vanilla CSS to generate a bespoke, premium aesthetic instead of overriding generic material libraries.

**Deferred Decisions (Post-MVP):**
- **Visual Analytics UI:** To be deferred to Phase 2 (Growth) after data exploitation grids are stabilized.

### Data Architecture

- **State Locality:** Local Route-level stores (SignalStore) will govern standard querying parameters per page. A singleton Global Store will be retained solely for the user's Geographic Cascade selections.

### Authentication & Security

- **JWT Intercession:** HTTP Interceptors will automatically flush the SignalStore global state and execute a logout route upon HTTP 401 exceptions.

### API & Communication Patterns

- **Asynchronous Execution Polling:** The background mass-export polling is encapsulated inside an `@Injectable()` service running an RxJS `timer().switchMap()` interval. This translates raw backend HTTP statuses into global, reactive Signals for the UI Toast.

### Frontend Architecture

- **Signal-Driven Patterns:** `RxJS` is strictly constrained to HTTP lifecycle and timed intervals. Complete DOM rendering will rely entirely on Angular 18 Native Signals (`computed`, `effect`, `@if`, `@for`).
- **Styling Architecture:** Global typography and color tokens defined in a root `index.css`, layered into BEM-structured Vanilla CSS for the Grid primitives.

### Infrastructure & Deployment

- **Compilation:** Ahead-of-Time (AOT) ESBuild static assets built out of Vite.

### Decision Impact Analysis

**Implementation Sequence:**
1. Scaffold Angular 18 workspace using the Native CLI parameters.
2. Establish API Interceptors and implement `ngx-permissions` bridging.
3. Configure the Global SignalStore state for the cascade dictionary.
4. Build out the Vanilla CSS Custom Data-grid component primitive.
5. Engineer the Async background Export poller service.

**Cross-Component Dependencies:**
- The Custom Grid component has a strict dependency on the active geography filters stored within the SignalStore. Filter manipulation must instantly trigger a grid redraw via an HTTP re-fetch.

## Implementation Patterns & Consistency Rules

### Pattern Categories Defined

**Critical Conflict Points Identified:**
4 critical areas where AI agents inevitably produce conflicting code blocks for modern Angular apps:
1. Component Standalone Scaffolding vs Legacy NgModules.
2. Signal vs RxJS boundary cross-contamination.
3. Vanilla CSS vs Inline Utility Frameworks.
4. Handling the Custom Java Backend Response Wrapper.

### Naming Patterns

**Code Naming Conventions:**
- **Signal Stores**: Must be named utilizing the `Store` syntax via `inject()` rather than classic class injection (e.g., `exportJobStore = inject(ExportJobStore)`).
- **Interfaces (Models)**: Must NOT use the `I` prefix. Use `Finding`, not `IFinding`. Interfaces must map exactly to the Spring Boot DTOs.

### Structure Patterns

**File Structure Patterns:**
- **Features**: Adhere strictly to grouped feature architecture. `/src/app/features/findings/list/findings-list.component.ts`.
- **Global Styles**: Global tokens and CSS variables map strictly to `/src/styles.css`. Scoped UI rules strictly reside in isolated `component.css`.

### Format Patterns

**API Response Formats (⚠️ CRITICAL FOR ALL AGENTS):**
All REST API endpoints from the `optimize-land-reg` backend wrap their actual data inside a custom standard envelope.
The frontend `HttpClient` calls **MUST** expect and unwrap this format precisely:
```typescript
interface ApiResponse<T> {
  status: string;
  statusCode: number;
  message: string;
  service: string;
  data: T;
}
// AI AGENT MANDATE: ALWAYS UNWRAP VIA `.data`
// return this.http.get<ApiResponse<Actor[]>>('/api/actors').pipe(map(res => res.data));
```

### Communication Patterns

**State Management Patterns:**
- **RxJS Constraints**: Only explicitly allowed inside `@Injectable()` API services for executing `HttpClient` requests or `timer()` polling.
- **Signal Constraints**: UI Components MUST consume state via Angular Signals (e.g., `myStore.entities()`). Agents are forbidden from exposing raw Observables to templates.

### Process Patterns

**Error Handling Patterns:**
- All network authorization errors (401, 403) from the Java `common-securities` layer must be intercepted globally via an `HttpInterceptorFn` (Angular 18 functional interceptor), triggering a Global Toast Signal and falling back to `/login`.

### Enforcement Guidelines

**All AI Agents MUST:**
- Scaffold strictly `standalone: true` components.
- Rely exclusively on modern Angular 18 Control Flow (`@if`, `@for`, `@empty`) in templates instead of legacy `*ngIf` / `*ngFor` directives.
- AVOID TailwindCSS or Bootstrap inline classes at all costs. Write pure CSS rules.

### Pattern Examples

**Good Examples:**
```html
<!-- STANDALONE NATIVE CONTROL FLOW & NATIVE SIGNALS -->
@for (actor of actors(); track actor.id) {
  <div class="actor-card">{{ actor.nup }}</div>
} @empty {
  <div class="empty-state">No actors found for this region.</div>
}
```

**Anti-Patterns (FORBIDDEN):**
```html
<!-- LEGACY ANGULAR WITH TAILWIND (BANNED) -->
<div *ngIf="actors$ | async as actors">
  <div *ngFor="let actor of actors" class="flex flex-col text-red-500">
    {{ actor.nup }}
  </div>
</div>
```

## Project Structure & Boundaries

### Complete Project Directory Structure

```text
manager-portal/
├── angular.json
├── package.json
├── tsconfig.json
├── src/
│   ├── index.html
│   ├── main.ts                       # Bootstraps the standalone app
│   ├── styles.css                    # Global Vanilla CSS tokens & resets
│   ├── app/
│   │   ├── app.component.ts          # Root router-outlet
│   │   ├── app.config.ts             # provideHttpClient, provideRouter
│   │   ├── app.routes.ts             # Lazy-loaded feature routes
│   │   ├── core/
│   │   │   ├── auth/
│   │   │   │   ├── interceptors/
│   │   │   │   │   └── custom-auth.interceptor.ts
│   │   │   │   ├── guards/
│   │   │   │   │   └── auth.guard.ts
│   │   │   │   └── services/
│   │   │   │       └── session.service.ts
│   │   │   ├── models/
│   │   │   │   └── api-response.model.ts  # Critical Backend Wrapper
│   │   │   └── store/
│   │   │       └── cascade-filter.store.ts   # Global SignalStore
│   │   ├── features/
│   │   │   ├── login/
│   │   │   ├── actors/
│   │   │   │   ├── list/
│   │   │   │   │   ├── actors-list.component.ts
│   │   │   │   │   └── actors-list.component.css
│   │   │   │   └── store/
│   │   │   │       └── actors.store.ts       # Feature SignalStore
│   │   │   ├── findings/
│   │   │   │   ├── list/
│   │   │   │   │   ├── findings-list.component.ts
│   │   │   │   │   └── findings-list.component.css
│   │   │   │   └── store/
│   │   │   │       └── findings.store.ts
│   │   │   └── synchro-history/
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   │   ├── data-grid/
│   │   │   │   │   ├── custom-grid.component.ts
│   │   │   │   │   └── custom-grid.component.css
│   │   │   │   ├── cascade-filter/
│   │   │   │   └── toast-notification/
│   │   │   └── services/
│   │   │       └── mass-export-poller.service.ts   # Async HTTP Polling Tracker
```

### Architectural Boundaries

**API Boundaries:**
- The frontend exclusively communicates with `/api/v1` routes exposed by the Spring Boot backend. 
- The `core/models/api-response.model.ts` is the absolute payload boundary; UI components never see raw HTTP responses.

**Component Boundaries:**
- Features (`actors`, `findings`) are strictly prohibited from importing each other.
- Shared Vanilla UI fragments (`custom-grid`, `cascade-filter`) never import Feature state; they rely purely on `@Input()` and `@Output()` bindings.

**State Boundaries:**
- `cascade-filter.store.ts` (Global): Provided in root. Holds the active geographic hierarchy. Features read from this to formulate backend query parameters.
- Feature Stores (Local): Provided at the feature-route level. They manage the volatile data arrays (e.g., the current page of 100 Actor rows).

### Requirements to Structure Mapping

**Feature/Epic Mapping:**
- **Identity & RBAC (FR1-FR5):** `src/app/core/auth/`
- **Actor Management (FR6-FR8):** `src/app/features/actors/`
- **Findings Grid (FR9-FR11):** `src/app/features/findings/`
- **Stateful Cascade Filtering (FR13-FR14):** `src/app/core/store/cascade-filter.store.ts` + `src/app/shared/components/cascade-filter/`
- **Mass CSV Export Polling (FR15-FR19):** `src/app/shared/services/mass-export-poller.service.ts`

**Cross-Cutting Concerns:**
- **Dynamic Action Shielding:** Components across the application will import `ngx-permissions` directives natively via standalone arrays.

### Integration Points

**Internal Communication:**
The `mass-export-poller.service.ts` listens for trigger events emitted by any Feature component, begins an explicit RxJS `timer` loop for HTTP short-polling, and projects the status down to the `toast-notification` component globally.

### File Organization Patterns

**Source Organization:**
- Feature directories bundle their dedicated UI components, SignalStore, and Data Models under one domain folder.
- The `shared/` directory is strictly reserved for "Dumb" presentation components and layout shells.

## Architecture Validation Results

### Coherence Validation ✅

**Decision Compatibility:**
All decisions integrate flawlessly. `@ngrx/signals` acts directly on Standalone APIs, completely bypassing historical `NgModule` boilerplate. `ngx-permissions` aligns effortlessly natively within modern component template arrays.

**Pattern Consistency:**
By mandating the unwrapping of the `ApiResponse<T>` standard payload from the Java Backend, we've proactively solved the primary cause of DTO mapping exceptions during frontend hydration.

**Structure Alignment:**
Isolating generic Custom Data Grids strictly inside `shared` while keeping business logic and State Stores strictly isolated inside `features` actively prevents unintended cyclic architecture dependencies.

### Requirements Coverage Validation ✅

**Epic/Feature Coverage:**
The custom directory structure establishes direct folders routing strictly to every Functional Category detailed in the `prd.md`.

**Functional Requirements Coverage:**
Via the `mass-export-poller.service.ts` singleton, the entire background HTTP generation requirements noted in FR15-FR21 are fully supported at the network architecture tier.

**Non-Functional Requirements Coverage:**
NFR performance (500ms guaranteed render) is highly achievable as Vanilla CSS restricts layout shift thrashing, and AOT ESBuild compilation maximizes initial browser paint speed.

### Implementation Readiness Validation ✅

**Decision Completeness:**
Every primary pillar (Execution CLI, State Management, DOM Flow, Rendering Framework) has definitive boundaries.

**Structure Completeness:**
Every relevant structural `store`, `component`, and `service` has been explicitly plotted for the scaffolding sprint.

**Pattern Completeness:**
Code style anti-patterns are well documented, prohibiting legacy `.subscribe()` template nesting in favor of native `.data()` Signal access.

### Gap Analysis Results

- **Minor Gap:** Standalone Form validation isn't deeply defined, though it's relatively unnecessary since this MVP is heavily biased toward data exploitation (dropdowns and search inputs) instead of heavy CRUD data-entry processing. (Resolution: Agents should naturally default to built-in `ReactiveFormsModule` when needed).

### Validation Issues Addressed

All major structural coherence checks officially passed.

### Architecture Completeness Checklist

**✅ Requirements Analysis**
- [x] Project context thoroughly analyzed
- [x] Scale and complexity assessed
- [x] Technical constraints identified
- [x] Cross-cutting concerns mapped

**✅ Architectural Decisions**
- [x] Critical decisions documented with versions
- [x] Technology stack fully specified
- [x] Integration patterns defined
- [x] Performance considerations addressed

**✅ Implementation Patterns**
- [x] Naming conventions established
- [x] Structure patterns defined
- [x] Communication patterns specified
- [x] Process patterns documented

**✅ Project Structure**
- [x] Complete directory structure defined
- [x] Component boundaries established
- [x] Integration points mapped
- [x] Requirements to structure mapping complete

### Architecture Readiness Assessment

**Overall Status:** READY FOR IMPLEMENTATION

**Confidence Level:** High. The boundaries explicitly shut down the common bad habits of LLM code generation (e.g. natively falling back to Bootstrap, Tailwind, RxJS in templates).

**Key Strengths:**
- Complete standalone isolation natively mirroring Angular 18's absolute finest capabilities.
- A bulletproof network boundary against Backend DTO mismatches via the `.data` extraction mandate.

**Areas for Future Enhancement:**
- Phase 2 (Growth) Analytics will necessitate architectural rules regarding charting dependency limits (e.g., picking D3.js vs Apache ECharts).

### Implementation Handoff

**AI Agent Guidelines:**
- Follow all architectural decisions exactly as documented
- Use implementation patterns consistently across all components
- Respect project structure and boundaries
- Refer to this document for all architectural questions

**First Implementation Priority:**
`npx @angular/cli@latest new manager-portal --style=css --routing=true --skip-tests=false --ssr=false`
