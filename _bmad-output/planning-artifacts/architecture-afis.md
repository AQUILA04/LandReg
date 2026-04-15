---
stepsCompleted: ['step-01-init', 'step-02-context.md', 'step-03-starter.md', 'step-04-decisions.md', 'step-05-patterns.md', 'step-06-structure.md', 'step-07-validation.md', 'step-08-complete.md']
inputDocuments: ['c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\planning-artifacts\prd.md', 'c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\project-context.md']
workflowType: 'architecture'
project_name: 'LandReg AFIS Vector Search'
user_name: 'Francis'
date: '2026-04-07T16:47:00+04:00'
status: 'complete'
completedAt: '2026-04-07T16:55:00+04:00'
---

# Architecture Decision Document

_This document builds collaboratively through step-by-step discovery. Sections are appended as we work through each architectural decision together._

## Project Context Analysis

### Requirements Overview

**Functional Requirements:**
Le système est un microservice Kafka-driven capable de recevoir des images d'empreintes, d'en extraire un vecteur mathématique via un modèle pre-entrainé ONNX, de réaliser un filtrage spatial HNSW ultra-rapide sur une base Qdrant, puis de conclure par une validation déterministe stricte 1:1 via l'algorithme Originel SourceAFIS. 
Architecturalement, cela structure le code en :
1. Une couche d'Ingestion asynchrone (Kafka Consumer & Producer).
2. Une couche de Calcul natif IA (JNI via ONNX Runtime dans la JVM).
3. Une couche de Persistance Vectorielle tierce (Client réseau gRPC Qdrant).

**Non-Functional Requirements:**
- **Performance & Latence :** < 250ms/requête au 95è percentile.
- **Stabilité Mémoire :** Maîtrise vitale et explicite de la RAM native (JNI) pour éviter l'Out-Of-Memory (OOM) historique des serveurs (isolation recommandée des workers).
- **Sécurité :** Milieu Air-Gap / VPC, aucune exposition HTTP directe des images biométriques.
- **Scalabilité & Tolérance aux pannes :** Ingestion lissée (Kafka Backpressure) sans timeout pour gérer les bursts de synchronisation terrain massifs ; utilisation stricte de *Dead Letter Queues* (DLQ).

**Scale & Complexity:**
Le load attendu (10 millions de profils, pics de 50k requêtes horaires) élève ce backend à une complexité quasi Event-Streaming/Big-Data.

- **Primary domain:** Event-Driven API Backend (High-Performance Biometrics)
- **Complexity level:** Very High (Integration de pointe IA native ONNX + Systèmes Distribués gRPC/Kafka)
- **Estimated architectural components:** 4 composants vitaux (Kafka Listeners, Tensor Injector/Manager, Qdrant gRPC Client, Legacy SourceAFIS Matcher).

### Technical Constraints & Dependencies
- Contrainte forte au standard environnemental : Java 17, Spring Boot 3, MapStruct, Lombok.
- Mode 'Brownfield' : Le système ne remplace par l'archivage source (MongoDB existant) ni la DB métier (PostgreSQL). Il s'intègre au processus existant de matching de façon chirurgicale.
- Isolation réseau (VPC/Air Gap) obligeant à concevoir une orchestration Docker autonome sans appel externe possible.

### Cross-Cutting Concerns Identified
- **Memory Leak Protection (Cœur du Réacteur) :** Toute ressource native ouverte (Tensor) doit être explicitement désallouée via un scope Java managé (`try-with-resources`).
- **Data Traceability :** Interdiction absolue de marquer un message Kafka en échec comme "lu" si un container distant a planté : les données biométriques sont non-rejouables depuis le terrain par l'agent.
- **Observability :** Les métriques HNSW vs SourceAFIS doivent être remontées nativement pour Grafana.

## Starter Template Evaluation

### Primary Technology Domain
API Backend (Java 17 / Spring Boot 3.4.1) basé sur une architecture **Brownfield** (projet existant `optimize-land-reg`).

### Starter Options Considered
Puisque le contexte est strictement Brownfield, la création d'un nouveau projet (via *Spring Initializr* ou *JHipster*) est formellement exclue. L'environnement d'exécution est déjà robuste et défini. L'évaluation du template porte donc sur la stratégie d'importation des nouvelles librairies et composants d'infrastructure.

### Selected Starter: Existing Spring Boot 3.4.1 Baseline

**Rationale for Selection:**
Le projet s'intègre chirurgicalement aux services existants (`FindingService`, `FindingController`). Créer un nouveau microservice imposerait de réécrire la gestion tenant/authentification Keycloak et la connexion hybride MongoDB/Postgres. Conserver la base Spring Boot 3 existante est l'unique choix viable.

**Initialization Command / Dependencies Update:**
L'intégration "Starter" consistera à updater le `pom.xml` avec les versions les plus récentes validées aujourd'hui :
```xml
<!-- Deep Learning Inference Engine -->
<dependency>
    <groupId>com.microsoft.onnxruntime</groupId>
    <artifactId>onnxruntime</artifactId>
    <version>1.24.3</version>
</dependency>

<!-- Qdrant Vector DB gRPC Client -->
<dependency>
    <groupId>io.qdrant</groupId>
    <artifactId>client</artifactId>
    <version>1.17.0</version>
</dependency>
```

**Architectural Decisions Provided by Existing Setup:**

**Language & Runtime:**
Java 17 (LTS), choix conservateur et éprouvé, essentiel pour garantir la stabilité de la consommation RAM lors des appels JNI (Native Interface vers ONNX C++).

**Build Tooling & Dependencies:**
Maven avec Spring Boot Starter Parent 3.4.1.

**Code Organization:**
Architecture N-Tiers classique (Controller, Service, Repository, Entity, DTO) incluant MapStruct pour la transformation sécurisée d'objets métier sans surcharge cognitive.

**Testing Framework:**
JUnit 5 couplé à Mockito, vital car le calcul HNSW Qdrant et l'inférence ONNX devront absolument être MOCKÉS dans la majorité des tests unitaires (sauf intégration).

**Development Experience:**
Lombok pour la réduction massive de boilerplate, permettant de garder les services `Matcher` et `Vectorizer` extrêmement clairs et testables.

## Core Architectural Decisions

### Decision Priority Analysis

**Critical Decisions (Block Implementation):**
- **Data Transport:** Apache Kafka allégé drastiquement par le pattern **Claim Check**.
- **Blob Storage (Claim Check):** Cluster S3 MinIO. Les messages Kafka surchargés par le Base64 sont abolis au profit de l'acheminement de chaînes URIs orientées vers les containers temporaires S3.
- **Core ML Inference Topology:** **In-Process**. L'inférence ONNX Runtime s'exécute nativement dans la JVM Spring Boot, plutôt que via une API Python REST séparée. Cela élimine la latence réseau inter-conteneurs et la surcharge excessive de sérialisation JSON pour un vecteur de 512 flottants.

**Important Decisions (Shape Architecture):**
- **Target Routing / Parallelism:** Orchestration éclatée — L'orchestrateur central `afis-master` reçoit le payload racine de `optimize-land-reg` et subdivise immédiatement le calcul en envoyant chaque URI de doigt à des instances concurrentes `afis-service`.
- **Vector Search Protocol:** Utilisation exclusive du Client **gRPC** Qdrant (port 6334) plutôt que l'API HTTP REST. Gain de performance radical sur le transit de tableaux de floats (sérialisés en binaire avec Protobuf).
- **Async Error Strategy:** Dead Letter Queue (DLQ) pattern obligatoire pour Kafka.
- **Security Validation:** Keycloak via Spring Security OAuth2 Resource Server.

**Deferred Decisions (Post-MVP):**
- **Reconciliation Batch Pattern:** Utilisation de Spring Batch vs Quartz vs Kafka Streams pour le nettoyage de nuit (repoussé à la Phase 2).

### Data Architecture
- **Vector DB:** Qdrant v1.17.x via image Docker officielle. Choisi pour son support natif de l'HNSW et sa performance brute écrite en Rust.
- **Blob Database:** Object Storage MinIO. Agit comme un tampon SAS sécurisé entre l'ingestion asynchrone (`queue-processing/`) et l'entrepôt vérifié validé par le système AFIS (`store/`).
- **Biometric Identity Storage:** MongoDB. N'indexe non plus nativement l'image mais préserve structurellement l'objet `FingerprintTemplate` de SourceAFIS.
- **Relational Metadata:** PostgreSQL via Spring Data JPA/Hibernate (infrastructure héritée existante).

### Authentication & Security
- **Inter-service Security:** Isolation complète Air-gap via réseau Docker VPC ou Kubernetes privé. Qdrant ne doit exposer aucun port sur l'hôte hôte de production.
- **Identity & Access Management:** Les webhooks ou APIs REST éventuelles (Actuator) sont sécurisés par validation de jetons JWT émis par l'instance **Keycloak** existante de LandReg.

### API & Communication Patterns
- **Messaging (Asynchronous):** Apache Kafka géré via les annotations `@KafkaListener` de Spring. L'acquittement d'offset (`AckMode.MANUAL_IMMEDIATE`) est obligatoire. On ne `commit` jamais manuellement un message avant que Qdrant a retourné un succès d'indexation.
- **Error Handling (DLQ):** Les exceptions métiers insolubles (ex: "Image biométrique totalement tronquée, inférence impossible") déroutent le message vers un topic Kafka `.dlq` de façon autonome, évitant un retraitement infini bloquant le thread (Poison Pill).

### Frontend Architecture
*Non applicable (Headless API Backend / Worker Kafka).*

### Infrastructure & Deployment
- **Containerization:** Provisionnement global local (`docker-compose.yml`) intégrant tous les services : PostgreSQL, MongoDB, Kafka/Zookeeper, Keycloak, Qdrant, et le build applicatif Java.
- **Storage Persistence:** Le mapping de `/qdrant/storage` vers un volume docker ou disque cloud SSD permanent est une décision infrastructurelle non-négociable (pour sauvegarder l'état HNSW).

### Decision Impact Analysis

**Implementation Sequence:**
1. Mise à jour critique de l'environnement : Upgrade propre du `docker-compose.yml` local pour intégrer Qdrant et configurer ses volumes.
2. Configuration YAML `application.yml` pour brancher `spring-kafka` et les credentials Keycloak existants.
3. Création du wrapper Tensor C++ => `FingerprintVectorizer` (ONNX) dans un environnement managé.
4. Création du `QdrantIndexService` via gRPC en interceptant les flux Kafka entrants.
5. Refonte finale du composant central `MatcherService` pour utiliser le proxy hybride (HNSW puis SourceAFIS).

**Cross-Component Dependencies:**
- Le composant AI (`FingerprintVectorizer`) va dépendre directement de l'isolation du *Thread Pool* de Kafka (`ConcurrentKafkaListenerContainerFactory`). Sur un afflux massif, une mauvaise limitation concurrentielle Kafka provoquerait une instabilité mémoire de la librairie native ONNX en dessous de la JVM. L'utilisation d'un Tuning "Backpressure" (Concurrency + `.setMaxPollRecords()`) est imposée.

## Implementation Patterns & Consistency Rules

### Critical Conflict Points Identified
Les agents IA travaillent sur le backend de façon asynchrone. Des divergences dans la gestion JNI/C++, le traitement Kafka, ou la politique d'encapsulation de l'API REST peuvent créer des régressions catastrophiques (fuites mémoire) ou casser l'interface côté Frontend.

### Naming Patterns

**Kafka Naming Conventions:**
- Topics format : `[domain].[action].[status]`. 
- Exemple: `biometrics.match.req`, `biometrics.match.res`, `biometrics.match.dlq`.

**Code Naming Conventions (ONNX Engine):**
- Variables Tensor : Inclusion obligatoire du mot *Tensor* pour repérer visuellement le cycle de vie natif et forcer à la fermeture. Exemples : `rawImageTensor`, `outputEmbeddingTensor`.

### Structure Patterns

**Project Organization:**
- Conservation de l'arborescence standard : `com.optimize.land.controller`, `.service`, `.repository`.
- Isolation des composants profonds : L'infrastructure C++ / ONNX ira dans un package dédié tel que `com.optimize.land.service.ai`. 

### Format Patterns

**API Response Formats:**
- **RÈGLE ABSOLUE** (Issue du Project Context) : Tout retour d'API (REST/JSON) doit être *exclusivement* encapsulé par le constructeur réseau pré-existant :
  `Response.builder().status(HttpStatus.OK).statusCode(HttpStatus.OK.value()).message("default.message.success").service("OPTIMIZE-SERVICE").data(data).build();`
  Absolument aucun retour brut ou `ResponseEntity` générique ne doit fuiter.

### Process & Security Patterns

**Memory Rules (JNI/ONNX - The Ultimate Rule):**
- Tout appel à `OrtEnvironment.getEnvironment()` ou création de composant ONNX (comme `OrtSession.SessionOptions`, `OnnxTensor`) DOIT vivre à l'intérieur d'un bloc `try-with-resources`. 
- Interdiction formelle de créer un Tensor hors scope de sécurité. Omission = Fuite mémoire C++ non détectable par Java.

**Error Handling & DLQ Kafka:**
- Dans toute méthode annotée `@KafkaListener`, on ne propage jamais d'exception sans traitement. Si le template biométrique reçu est corrompu, l'exception est interceptée localement, le payload défaillant est posté vers la DLQ via `KafkaTemplate`, et l'acquittement JCA (`Acknowledgment.acknowledge()`) est invoqué pour faire avancer le pointeur, annihilant ainsi toute *Poison Pill* infinie.

## Project Structure & Boundaries

### Complete Project Directory Structure

```text
optimize-land-reg/
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── src/
│   ├── main/
│   │   ├── java/com/optimize/land/
│   │   │   ├── config/             # Kafka, Qdrant, Security, ONNX configs
│   │   │   ├── controller/         # REST API (Actuator endpoints mainly)
│   │   │   ├── dto/                # Kafka event / REST payloads
│   │   │   ├── exception/          # Global error handling
│   │   │   ├── model/
│   │   │   │   ├── entity/         # JPA Entities
│   │   │   │   └── mapper/         # MapStruct interfaces
│   │   │   ├── repository/         # Spring Data JPA
│   │   │   ├── service/            # Core business logic
│   │   │   │   ├── ai/             # FingerprintVectorizer (ONNX wrapper)
│   │   │   │   ├── kafka/          # Message listeners and publishers
│   │   │   │   └── qdrant/         # gRPC client for Qdrant DB
│   │   │   └── OptimizeLandApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-prod.yml
│   │       ├── models/             # Local .onnx files for dev
│   │       └── db/migration/       # Flyway SQL scripts
│   └── test/
│       ├── java/com/optimize/land/
│       │   ├── service/            # Unit tests (mocking ONNX/Qdrant)
│       │   └── integration/        # Testcontainers for Kafka/Qdrant tests
│       └── resources/
│           ├── application-test.yml
│           └── sample_images/      # Local WSQ/PNG samples
```

### Architectural Boundaries

**API Boundaries:**
- Aucun port public n'est exposé pour consommer de la donnée. La seule communication entrante asynchrone est Kafka. L'API REST standard ne sert qu'au Prometheus scraping local sur `/actuator/metrics`.

**Component Boundaries (Information Hiding):**
- Le package `service.ai` encapsule massivement l'implémentation C++/ONNX. Les autres composants du système sont totalement agnostiques de la présence du Runtime ONNX. L'interface `vectorize()` rend un simple objet/tableau Java, les abstractions natives s'effondrent à l'intérieur de ce sous-domaine de manière étanche.

**Data Boundaries:**
- Qdrant est l'unique maître source pour statuer sur le "Voisinage" / la similarité biométrique approchée.
- PostgreSQL reste l'unique maître de l'Acteur et garant de l'atomicité de ses décisions administratives.

### Requirements to Structure Mapping

**Feature: High-Speed Vectorization (FR1)**
- Localisation : `src/main/java/com/optimize/land/service/ai/`
- Consigne : Couverture Test unitaire isolée impérative (sans démarrer le runtime natif continuellement).

**Feature: Spatial HNSW Searching (FR3)**
- Localisation : `src/main/java/com/optimize/land/service/qdrant/`
- Consigne : Utiliser protobuf/gRPC pour la vitesse absolue.

**Cross-Cutting Concerns:**
- **Observability:** `src/main/java/com/optimize/land/config/MetricsConfig.java` enregistre les Timers Kafka globaux.

## Architecture Validation Results

### Coherence Validation ✅

**Decision Compatibility:**
Toutes les décisions sont structurellement et temporellement compatibles. Spring Boot 3.4.1 gère nativement le backpressure Kafka (DQL) via les `ConcurrentKafkaListenerContainerFactory`. L'intégration du client gRPC Qdrant (v1.17) est standard, et la librairie `onnxruntime` Java (1.24.3) s'exécute parfaitement en In-Process sans dépendance Python externe.

**Pattern Consistency:**
Les patterns de gestion mémoire (blocs `try-with-resources` obligatoires pour JNI) sont définis comme règle d'or, verrouillant le seul vrai risque de l'architecture hybride Java/C++.

**Structure Alignment:**
La structure du projet `optimize-land-reg` isole strictement les dépendances IA (`service.ai`) du reste du domaine, garantissant une refactorisation possible à l'avenir vers un autre moteur de deep learning si nécessaire.

### Requirements Coverage Validation ✅

**Functional Requirements Coverage:**
Le contrat du PRD (MVP : Vectorisation IA + Recherche Top-100 HNSW + Validation SourceAFIS) est entièrement cartographié dans le cycle `KafkaListener` -> `FingerprintVectorizer` -> `QdrantIndexService` -> `MatcherService`.

**Non-Functional Requirements Coverage:**
Performance (NFR-PERF-1) traitée par l'abandon du REST/JSON au profit de Kafka + gRPC. Sécurité de transport (NFR-SEC-2) adressée par le déploiement full Docker/VPC Air-gap.

### Implementation Readiness Validation ✅

La documentation des décisions est exhaustive. Le squelette de l'application Brownfield est identifié. 

### Architecture Completeness Checklist
- [x] Project context thoroughly analyzed
- [x] Critical decisions (Kafka, In-Process ONNX, gRPC Qdrant) documented with versions
- [x] Implementation patterns (Memory Leak prevention) established
- [x] Complete directory structure mapped on existing codebase

### Architecture Readiness Assessment
**Overall Status:** READY FOR IMPLEMENTATION

**Confidence Level:** Haute. L'approche Brownfield permet d'avoir la base métier, le backend base de données (Postgres) et la sécurité d'accès interne (Keycloak) déjà établis.

**First Implementation Priority:**
Mise à jour de l'infrastructure Docker (`docker-compose.yml`) pour supporter `qdrant/qdrant` et implémentation du `pom.xml` pour `onnxruntime` et l'intercepteur gRPC.
