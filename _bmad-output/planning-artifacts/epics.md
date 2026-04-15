---
stepsCompleted: ['step-01-validate-prerequisites', 'step-02-design-epics', 'step-03-create-stories', 'step-04-final-validation']
inputDocuments: ['c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\planning-artifacts\prd.md', 'c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\planning-artifacts\architecture.md', 'c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\planning-artifacts\ux-design-specification.md']
---

# LandReg Admin Portal - Epic Breakdown

## Overview
This document provides the complete epic and story breakdown for LandReg Admin Portal, decomposing the requirements from the PRD, UX Design if it exists, and Architecture requirements into implementable stories.

## Requirements Inventory
### Functional Requirements
FR1: A User can securely authenticate into the portal using `common-securities` credentials.
FR2: A SUPERADMIN can create, view, update, and disable User Accounts.
FR3: A SUPERADMIN can create, view, update, and disable User Profiles (RBAC Roles).
FR4: A SUPERADMIN can assign specific granular permissions.
FR5: The System dynamically restricts UI components via Profile constraints.
FR6: An ADMIN can view a paginated list of registered `Actors`.
FR7: An ADMIN can view a paginated list of submitted `Findings` (Constatations).
FR8: An ADMIN can view a paginated list of global `SynchroHistory` records.
FR9: An ADMIN can filter data grids dynamically by selecting a 'Region'.
FR10: An ADMIN can filter data grids by 'Prefecture'.
FR11: An ADMIN can filter data grids by 'Commune'.
FR12: An ADMIN can filter data grids by 'Canton'.
FR13: An ADMIN can filter data grids chronologically.
FR14: An ADMIN can stack and combine filters simultaneously.
FR15: An ADMIN can generate mass data export asynchronously.
FR16: An ADMIN can select CSV or XLSX formats.
FR17: The System executes export natively in the background.
FR18: The System displays an active tracking UI element.
FR19: The System displays file delivery success securely.
FR20: ADMIN can download the payload directly.
FR21: System generates permanent audit events dynamically.

### NonFunctional Requirements
NFR-PERF-1: 500ms grid render interactions.
NFR-PERF-2: HTTP polling executes with minimal payload.
NFR-SEC-1: Global routing `HttpInterceptor` strictly traps JWT rules.
NFR-SEC-2: Direct database bypassing is inherently forbidden.
NFR-SCA-1: Frontend forces scalable remote chunking instead of full downloads.
NFR-UX-1: Bespoke Vanilla CSS rules natively over browser defaults.

### Additional Requirements
- Architecture Setup: `npx @angular/cli@latest new manager-portal --style=css --routing=true --skip-tests=false --ssr=false`
- Auth Bridge: `ngx-permissions` maps directly to REST structures.
- Backend Contract: Isolate all API parsing specifically with `.data` to bypass Spring DTO constraints.

### UX Design Requirements
UX-DR1: Implement "Dark Analytics" `--color-primary` CSS tokens globally.
UX-DR2: Implement `<app-data-grid>` component structures supporting Custom properties.
UX-DR3: Construct the isolated `<app-geo-cascade>` element handling global `@ngrx/signals`.
UX-DR4: Build tracking overlay `<app-toast-manager>`.
UX-DR5: Target "Command Center" structural viewport wrapper natively.

### FR Coverage Map
FR1: Epic 1 - Secure authentication via `common-securities`.
FR2: Epic 1 - SUPERADMIN manages User Accounts.
FR3: Epic 1 - SUPERADMIN manages User Profiles (RBAC).
FR4: Epic 1 - SUPERADMIN assigns explicit granular permissions.
FR5: Epic 1 - System restricts rendering dynamically via profiles.
FR6: Epic 2 - ADMIN views paginated Actors grid.
FR7: Epic 2 - ADMIN views paginated Findings grid.
FR8: Epic 2 - ADMIN views paginated SynchroHistory grid.
FR9: Epic 2 - Omni-Filter Region constraints.
FR10: Epic 2 - Omni-Filter Prefecture constraints.
FR11: Epic 2 - Omni-Filter Commune constraints.
FR12: Epic 2 - Omni-Filter Canton constraints.
FR13: Epic 2 - Chronological filter declarations.
FR14: Epic 2 - Filter stacking combination.
FR15: Epic 3 - Command system to generate mass export.
FR16: Epic 3 - Toggle CSV/XLSX configurations.
FR17: Epic 3 - Execute async data aggregation and file generation natively.
FR18: Epic 3 - Display tracking active polling states.
FR19: Epic 3 - Display background completion native visual notifications.
FR20: Epic 3 - Securely serve specific payloads for download via Toasts.
FR21: Epic 3 - Audit export trails permanently upon generation.

## Epic List
### Epic 1: Foundation & Identity
**Goal:** Establish the core secure application shell. Admins can securely log in, navigate via the Command Center layout, and SuperAdmins can manage access boundaries and custom profiles across the administration team.
**FRs covered:** FR1, FR2, FR3, FR4, FR5

### Epic 2: The Core Data Grids & Omni-Filter
**Goal:** Admin operators can instantly consult records and seamlessly drill down using the global Geographic Cascade and Chronological filters to pinpoint specific subsets of land registration data.
**FRs covered:** FR6, FR7, FR8, FR9, FR10, FR11, FR12, FR13, FR14

### Epic 3: Asynchronous Extraction Engine
**Goal:** Admin operators can confidently trigger mass dataset exports to CSV/XLSX, reliably tracking progress via a non-blocking background toast notification while an immutable audit log is generated.
**FRs covered:** FR15, FR16, FR17, FR18, FR19, FR20, FR21

## Epic 1: Foundation & Identity
Establish the core secure application shell. Admins can securely log in, navigate via the Command Center layout, and SuperAdmins can manage access boundaries and custom profiles across the administration team.

### Story 1.1: Standalone Architecture & Core Layout Shell
As a Developer,
I want to bootstrap the Angular 18 Standalone application with the Vanilla CSS Command Center Layout,
So that all future components can inherit the validated Dark Analytics design tokens natively.

**Acceptance Criteria:**
**Given** an empty repository
**When** the project is scaffolded using `@angular/cli@latest`
**Then** the `npx @angular/cli@latest new manager-portal` strictly without NgModules
**And** the `index.css` is populated with the predefined Dark Analytics variables, 4px spacing rules, and 13px Inter typography
**And** a global Application Shell component is implemented ensuring the top-horizontal layout wrapper.

### Story 1.2: Authentication Enforcement & HTTP Interceptor
As a Security Context,
I want to automatically trap and route unauthenticated traffic with an HttpInterceptor,
So that users are forced to authenticate securely using `common-securities` JWT logic.

**Acceptance Criteria:**
**Given** a user is unauthenticated or has an expired session
**When** they attempt to load any frontend route or request API data
**Then** the global Angular HttpInterceptor identifies the 401 Unauthorized anomaly
**And** the user is gracefully redirected to the `/login` portal
**And** the system successfully validates the JWT against the backend API wrapper (extracting `res.data`).

### Story 1.3: Custom Profiles & RBAC Configuration
As a SUPERADMIN,
I want to manage UI Profiles and configure specific access rights (e.g., EXPORT_DATA),
So that data-governance roles natively protect sensitive actions directly in the DOM.

**Acceptance Criteria:**
**Given** the user holds the SUPERADMIN permission context
**When** they navigate to the RBAC Management View
**Then** they can create/read/update profiles with specific granular permissions attached
**And** the `ngx-permissions` library successfully parses these rights and dynamically mounts/unmounts structural frontend elements globally.

### Story 1.4: Administrative User Management
As a SUPERADMIN,
I want to create and assign Operator accounts to specific Profiles,
So that agents within the ministry receive their customized portal credentials securely.

**Acceptance Criteria:**
**Given** the user holds SUPERADMIN clearance
**When** creating or updating an operator Account
**Then** they can assign a predefined Profile structure natively
**And** track the enabled/disabled statuses of those portal accounts.

## Epic 2: The Core Data Grids & Omni-Filter
Admin operators can instantly consult records and seamlessly drill down using the global Geographic Cascade and Chronological filters to pinpoint specific subsets of land registration data.

### Story 2.1: The Omni-Filter Cascade & Global Store
As a Data Operator,
I want to interact with a reactive 4-tier geographic dropdown (Region -> Prefecture -> Commune -> Canton),
So that I can drill into precise locations effortlessly with shared global persistence.

**Acceptance Criteria:**
**Given** the Omni-Filter is rendered in the global Layout Top-Bar
**When** the user selects a 'Region'
**Then** the `@ngrx/signals` Global Store captures the state actively
**And** the 'Prefecture' dropdown optimisticially unlocks, waiting for the subsequent API options
**And** stacking filter decisions (Chronological Date ranges) integrates instantly into a clean query object accessible by all Grid components.

### Story 2.2: Vanilla Custom Data Grid Primitive
As a Frontend Experience,
I want a highly performant, custom data grid utilizing HTML5 tables and strict Custom Properties,
So that rows render efficiently without locking the browser UI thread or bloating the stylesheet.

**Acceptance Criteria:**
**Given** tabular matrix requirements for the analytics views
**When** developers invoke the `<app-data-grid>` custom component
**Then** it natively supports precise 13px typographies, customized scroll-bars with sticky table head sections, and zebra-striping rows
**And** includes Skeleton Loaders displaying during 500ms fetch intervals rather than full-page blocking spinners
**And** provides an explicit 'Empty Dataset' `<tr aria-label="...">` fallback if zero rows return.

### Story 2.3: Actors Directory Subsystem
As an ADMIN,
I want to consult the exhaustive paginated directory of biological Actors,
So that I can verify field-recorded land-registration operators against the geographical cascade filters.

**Acceptance Criteria:**
**Given** a user has 'VIEW_ACTORS' structural privileges
**When** navigating to the `/actors` grid path
**Then** the system requests server-side chunks of 50/100/500 Actors dynamically reacting exclusively to the Global Omni-Filter
**And** gracefully handles API mapping parsing solely through the `.data` payload syntax
**And** avoids caching millions of rows locally, complying entirely with strict Server-Side pagination conventions.

### Story 2.4: Findings (Constatations) Directory Subsystem
As an ADMIN,
I want to analyze the logged land Constatations mapped by mobile field agents,
So that the regional administration can review coordinates, metrics, and progress locally.

**Acceptance Criteria:**
**Given** the user parses Constatation data
**When** they select a chronological Start/End frame
**Then** the API responds specifically with findings registered in that timespan for the declared Geographic filter node.

### Story 2.5: SynchroHistory Global Audit Grids
As a Governance Officer,
I want to explore the history of synchronization batches performed by field devices,
So that missing device batches can strictly be identified and reconciled internally.

**Acceptance Criteria:**
**Given** access to the Synchro module
**When** the query loads into the Data Grid primitive
**Then** the operator clearly recognizes batch boundaries, device IPs, and temporal logs within sub-500ms render targets.

## Epic 3: Asynchronous Extraction Engine
Admin operators can confidently trigger mass dataset exports to CSV/XLSX, reliably tracking progress via a non-blocking background toast notification while an immutable audit log is generated.

### Story 3.1: Export Toast Manager & Short-Polling Service
As a System Process,
I want to track long-running mass-background generation jobs via an RxJS interval HTTP short-polling loop,
So that the UI can project a persistent Toast tracking element showing the progress of external queries.

**Acceptance Criteria:**
**Given** a mass data extraction begins on the architecture
**When** the backend generates an initial tracked Job ID
**Then** the `mass-export-poller.service.ts` actively subscribes to an interval polling check 
**And** structurally updates a global tracking `<app-toast-manager>` sitting non-blockingly in the bottom-right corner.

### Story 3.2: Extraction Execution & Immutable Audits
As an ADMIN,
I want to click Export to CSV/XLSX against my heavily filtered active data grid,
So that the backend compiles the document remotely and instantly registers a permanent Audit Log.

**Acceptance Criteria:**
**Given** the user holds `EXPORT_DATA` authorizations globally
**When** they select "Export Mass File" indicating either an XLSX or CSV target
**Then** the Java API traps the request locally, acknowledging it into the queue
**And** the portal triggers an irreversible audit trail transaction including the User ID, Timestamp, and actual Filter conditions at that instant.

### Story 3.3: Secured File Delivery Access
As an ADMIN,
I want the Toast notification to visually alter when the generation completes,
So that I can click it natively to trigger a secure file stream download into my system.

**Acceptance Criteria:**
**Given** the background task API officially signals the job sequence is 'DONE'
**When** the polling module reflects '100% complete'
**Then** the Toast Manager transitions distinctly (e.g. green pulse border) showing an active link
**And** the operator can download the blob artifact efficiently using standard web browser handlers.
