---
stepsCompleted: ['step-01-init', 'step-02-discovery', 'step-02b-vision', 'step-02c-executive-summary', 'step-03-success', 'step-04-journeys', 'step-05-domain', 'step-06-innovation', 'step-07-project-type', 'step-08-scoping', 'step-09-functional', 'step-10-nonfunctional', 'step-11-polish']
inputDocuments:
  - 'c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\project-context.md'
  - 'c:\Users\kahonsu\Documents\GitHub\LandReg\docs\class-diagram.mermaid'
workflowType: 'prd'
classification:
  projectType: 'Web Application (Admin Back-Office)'
  domain: 'Land Registration / Public Administration Data'
  complexity: 'Medium'
  projectContext: 'brownfield'
---

# Product Requirements Document - LandReg Admin Portal

**Date:** 2026-04-09
**Domain:** Land Registration / Public Administration Data
**Complexity:** Medium

## 1. Executive Summary
The LandReg Admin Portal is a dedicated back-office web application designed for data operators (ADMIN role) to safely consult, manage, and extract land registration records (Actors, Constatations, and SynchroHistory) captured by mobile field applications. 

By replacing ad-hoc mobile data views with a robust, desktop-grade web interface, the system directly connects administrators to the secure `optimize-land-reg` data APIs. Its core differentiator is an uncompromising specialization in high-fidelity, multidimensional search—specifically deep geographic cascading (Region -> Prefecture -> Commune -> Canton) and chronological filtering—applied uniformly across on-screen data tables and reliable mass CSV/XLSX exports.

## 2. Success Criteria
- **User Success:** Administrators and managers can effortlessly stack complex geographic and chronological filters, triggering heavy data exports without interface lockups.
- **Business Success:** Complete operational delineation; mobile apps remain strictly for field-entry, while the web portal becomes the exclusive tool for desk-based data analysis.
- **Technical Success:** Zero interface timeouts during massive cross-regional data extractions, guaranteed by a strict asynchronous export pipeline.

## 3. Product Scope & Phased Development

### MVP Strategy (Phase 1)
Delivering uncompromising reliability and speed in bulk data extraction to officially deprecate the use of field mobile tools for administrative desk work. 
- Standalone Angular 18 application shell.
- Reusable custom RBAC Management UI directly integrating with existing `common-securities`.
- High-performance Data Grids for `Actors`, `Findings`, and `SynchroHistory`.
- Dynamic Geographic Cascade Filtering and chronological constraints.
- Asynchronous CSV/XLSX Export engine coupled with HTTP short-polling notifications.

### Growth & Expansion (Phase 2 & 3)
- **Phase 2:** Visual analytics, aggregate KPI dashboards, and dynamic data visualization tied directly to grid filters.
- **Phase 3:** Real-time comprehensive spatial monitoring (GIS map integration) and predictive automated report distribution.

## 4. User Journeys

### 4.1 The Regional Data Audit (Standard Path)
**Persona:** Amina, an `ADMIN` Data Operator. 
**Scenario:** End-of-month verification of land constatations for the "Maritime" region ("Lomé" prefecture) before billing.
**Journey:**
1. Logs securely into the web portal via `common-securities`.
2. Uses geographic cascade filters and the a 30-day chronological filter on the "Constatations" module.
3. Reviews the instantly rendered data grid.
4. Clicks "Export to XLSX", receiving a notification toast moments later to safely download the data.

### 4.2 The National Extract (Heavy Workload)
**Persona:** David, a Senior Operations Manager.
**Scenario:** The Ministry requests a massive dataset of all biological Actors enrolled nationally over the year.
**Journey:**
1. Applies the 1-year filter on the "Actors" grid and clicks "Export to CSV".
2. The UI confirms the background extraction is underway without freezing his browser.
3. David continues working in other tabs.
4. An in-app notification alerts him when the file is compiled, allowing a secure download.

### 4.3 The Access Controller
**Persona:** Thomas, an IT `SUPERADMIN`.
**Scenario:** Creating a "Read-Only" department profile that strictly disables export capabilities.
**Journey:**
1. Navigates to the RBAC Management Module utilizing his `common-securities` credentials.
2. Creates a new Profile, toggling off the discrete `EXPORT_DATA` permission. 
3. The UI dynamically strips "Export" buttons for operators assigned to this profile.

## 5. Architectural & Domain Constraints

### 5.1 Technical Architecture
- **Application Structure:** Built entirely from scratch as a standalone Angular 18 Host application (decoupled from the legacy `afis-master` micro-frontend shell).
- **Asynchronous Isolation:** Background mass-export tasks are strictly isolated from synchronous web requests to eliminate Out-Of-Memory (OOM) risks and Gateway timeouts.
- **Polling Engine:** Export execution status is tracked via a lightweight HTTP short-polling mechanism to balance UX requirements with backend resource efficiency.
- **Data Governance:** The system natively adheres to `common-securities` for all access control boundaries with no additional attribute-level data-masking required.

### 5.2 Implementation Standards
- **Modern UI/UX Strategy:** The UI must deliver a premium, dynamic user experience. It mandates avoiding generic browser defaults and obsolete third-party UI libraries/Tailwind classes, instead utilizing sophisticated typography, deliberate color contrast, smooth layouts, and responsive micro-animations globally.

## 6. Functional Requirements (Capabilities Contract)

### Identity & Access Management
- **FR1:** A User can securely authenticate into the portal using `common-securities` credentials.
- **FR2:** A SUPERADMIN can create, view, update, and disable User Accounts.
- **FR3:** A SUPERADMIN can create, view, update, and disable User Profiles (RBAC Roles).
- **FR4:** A SUPERADMIN can assign specific granular permissions (e.g., `EXPORT_DATA`) to User Profiles.
- **FR5:** The System dynamically restricts UI components and data access based on the User's authorized Profile constraints.

### Data Consultation & Navigation
- **FR6:** An ADMIN can view a paginated list of registered `Actors`.
- **FR7:** An ADMIN can view a paginated list of submitted `Findings` (Constatations).
- **FR8:** An ADMIN can view a paginated list of global `SynchroHistory` records.

### Advanced Filtering Constraints
- **FR9:** An ADMIN can filter data grids dynamically by selecting a 'Region'.
- **FR10:** An ADMIN can filter data grids by 'Prefecture', strictly constrained by the active 'Region' selection.
- **FR11:** An ADMIN can filter data grids by 'Commune', strictly constrained by the active 'Prefecture' selection.
- **FR12:** An ADMIN can filter data grids by 'Canton', strictly constrained by the active 'Commune' selection.
- **FR13:** An ADMIN can filter data grids chronologically by declaring a specific 'Start Date' and 'End Date'.
- **FR14:** An ADMIN can stack and combine geographic and chronological filters simultaneously.

### Asynchronous Extraction Engine
- **FR15:** An ADMIN can command the system to generate a mass data export of the currently active filtered grid.
- **FR16:** An ADMIN can select either CSV or XLSX as the designated mass export format.
- **FR17:** The System executes the data aggregation and file generation in the background without blocking the user interface.
- **FR18:** The System displays an active status indicator to the user while an export task is processing.
- **FR19:** The System generates a reliable UI notification for the user the moment the background export successfully completes.
- **FR20:** An ADMIN can securely download the generated CSV/XLSX payload directly via the completion notification.

### Audit & Governance
- **FR21:** The System automatically generates a permanent audit record (logging the User ID, Timestamp, and all applied Filters) the exact moment a mass export is initiated.

## 7. Non-Functional Requirements

### Performance
- **NFR-PERF-1 (UI Responsiveness):** Geographic cascade filter selections and data-grid page transitions must fetch and render within 500 milliseconds (at the 95th percentile).
- **NFR-PERF-2 (Polling Efficiency):** The HTTP short-polling mechanism used to check export statuses must execute with minimal payload overhead, ensuring it does not degrade the local operator's network bandwidth.

### Security
- **NFR-SEC-1 (Token Enforcement):** All frontend routes must strictly enforce JWT/Session validation natively via the `common-securities` integration, instantly redirecting unauthorized or expired sessions to the login screen.
- **NFR-SEC-2 (API Isolation):** The administrative web portal must never bypass the `optimize-land-reg` REST APIs to access the database directly; it must respect all backend security contexts inherently.

### Scalability (Data Handling)
- **NFR-SCA-1 (Server-Side Optimization):** The frontend data grids must mandate strict server-side pagination and filtering. It must absolutely never attempt to download full datasets into the local browser memory, guaranteeing stable UX performance even when querying tens of millions of records.

### Usability & Aesthetics
- **NFR-UX-1 (Design Standards):** In accordance with the Project Rules, the UI must implement a premium design aesthetic built from scratch. It must avoid generic browser defaults, utilizing modern typography, deliberate color contrast for operators reading screens all day, and smooth micro-animations to communicate async system states clearly.
