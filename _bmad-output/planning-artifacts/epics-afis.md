---
stepsCompleted: ['step-01-validate-prerequisites.md', 'step-02-design-epics.md', 'step-03-create-stories.md', 'step-04-final-validation.md']
inputDocuments: ['c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\planning-artifacts\prd.md', 'c:\Users\kahonsu\Documents\GitHub\LandReg\_bmad-output\planning-artifacts\architecture.md']
status: 'complete'
---

# LandReg AFIS Vector Search - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for LandReg AFIS Vector Search, decomposing the requirements from the PRD, UX Design if it exists, and Architecture requirements into implementable stories.

## Requirements Inventory

### Functional Requirements

FR1: Le Système peut extraire un vecteur mathématique (Embedding) à partir d'une image d'empreinte digitale encapsulée dans le modèle Kafka existant.
FR2: Le Système peut indexer le vecteur biométrique dans le moteur de recherche asynchrone (Qdrant).
FR3: Le Système peut pré-filtrer la base pour isoler un sous-ensemble (Top 100) en interrogeant l'index vectoriel HNSW.
FR4: Le Système peut appliquer la comparaison déterministe (SourceAFIS, 1:1) exclusivement sur les candidats pré-filtrés.
FR5: Le Système gère l'échec d'extraction vectorielle isolément pour ne pas interrompre l'ingestion Kafka globale.
FR6: L'Opérateur peut surveiller sélectivement les phases d'extraction, HNSW et validation via des métriques dédiées.

### NonFunctional Requirements

NFR-PERF-1: (Latence) Temps de traitement global < 250ms (95è percentile).
NFR-PERF-2: (Mémoire) Prévention des fuites RAM natives via le strict monitoring ONNX dans la JVM (seuil < 2GB/worker).
NFR-SEC-1: (Confidentialité) Purge RAM immédiate des objets image décodés après vectorisation.
NFR-SEC-2: (Réseau) VPC / Déploiement inter-cluster interne exclusif (port Qdrant gRPC interne).
NFR-SCA-1: (Volume) Qdrant dimensionné pour supporter 10 000 000 d'entrées vecteurs 512-dim.
NFR-SCA-2: (Flux) Backpressure Kafka obligatoire pour absorber les bursts de connexion.
NFR-REL-1: (Robustesse) Interdiction du commit asynchrone de l'offset Kafka tant que Qdrant ne valide pas son opération.

### Additional Requirements

- Mise à jour du `docker-compose.yml` local pour instancier Qdrant v1.17.0 de façon isolée.
- Importation Maven stricte : `onnxruntime` v1.24.3, `qdrant-client` v1.17.0 (gRPC).
- Architecture de composants : Implémentation du pattern `try-with-resources` non-négociable pour la classe englobant les `OnnxTensor`.
- Routing des erreurs insolubles (images totalement tronquées empêchant l'inférence ONNX) vers le topic Kafka de type Dead-Letter-Queue (`*.dlq`).

### UX Design Requirements

N/A (Projet Backend Headless - API/Event-driven)

### FR Coverage Map

FR1: Epic 1 - Extraction Embedding ONNX
FR2: Epic 1 - Indexation Qdrant Asynchrone
FR3: Epic 2 - Index HNSW Pré-filtrage Top 100
FR4: Epic 2 - Validation SourceAFIS exclusive
FR5: Epic 1 - Gestion échec/DLQ isolation
FR6: Epic 3 - Monitoring ciblé
FR7: Epic 1/2 - Allègement de charge Kafka via Claim-Check MinIO

## Epic List

### Epic 1: Moteur d'Ingestion Vectorielle et Claim-Check
Le système libère Kafka du transfert base64 (pattern Claim-Check). `optimize-land-reg` transfère les images de chaque doigt sur S3 Minio, puis initie un Array d'URIs. `afis-master` dispatche chaque URI individuellement aux workers `afis-service` qui gèrent l'extraction ONNX et le stockage Qdrant de chaque doigt.
**FRs covered:** FR1, FR2, FR5, FR7

### Epic 2: Pipeline de Déduplication Hybride Distribué
Le worker `afis-service` télécharge son image pointée, lance une recherche HNSW ciblée, convertit en `FingerprintTemplate` et valide via SourceAFIS contre son Top 100. Il renvoie le statut unitaire au Master. `afis-master` consolide les résultats des doigts de l'individu : sans doublon, il promeut le répertoire S3 de `queue-processing/` vers `store/` et persiste les templates AFIS dans Mongo. Sinon il détruit le temporaire.
**FRs covered:** FR3, FR4, FR7

### Epic 3: Observabilité et Télémétrie Opérationnelle
Les administrateurs peuvent diagnostiquer les performances asynchrones (S3 fetch, ML ONNX, Qdrant et SourceAFIS).
**FRs covered:** FR6

## Epic 1: Moteur d'Ingestion Vectorielle et Claim-Check

Le système libère Kafka du poids des base64 en utilisant MinIO comme point central de transit, puis vectorise les empreintes individuellement et asynchroneusement.

### Story 1.1: Initialisation de l'infrastructure Vectorielle et Blob Storage

As a développeur système,
I want intégrer `onnxruntime`, `qdrant-client` et le SDK S3 (`minio`) via Maven et upgrader le cluster docker local,
So that le microservice LandReg ait l'intégralité du socle S3 et vectoriel pour soutenir son architecture distribuée.

**Acceptance Criteria:**

**Given** le fichier `docker-compose.yml` du projet
**When** le développeur ajoute l'image docker officielle `qdrant/qdrant` et `minio/minio`
**Then** Qdrant (6334) et MinIO (9000 et console sur 9001) démarrent sans encombre
**And** l'application Spring Boot compile sans conflit avec des buckets S3 créés au démarrage (`queue-processing`, `store`).

### Story 1.2: Refonte de l'Hébergement via Claim-Check (`optimize-land-reg`)

As a producteur de données Kafka de première ligne,
I want que chaque demande de déduplication uploade en lots ses images vers MinIO sous `queue-processing/{rid}/{finger_id}`, puis n'insère formellement que les URIs dans Kafka,
So that l'empreinte mémoire d'un message retombe instantanément sous le seuil critique des quelques kilooctets.

**Acceptance Criteria:**

**Given** la capture biométrique validée d'un individu (plusieurs doigts) depuis le client
**When** `optimize-land-reg` structure la demande asynchrone
**Then** chaque donnée brute est uploadée au travers du SDK S3 vers `queue-processing/{rid}`
**And** le message Kafka envoyé contient exclusivement les pointeurs URI générés pour alerter `afis-master`.

### Story 1.3: Extraction ONNX In-Process et Indexation (afis-service)

As a module worker d'`afis-service`,
I want récupérer l'image via son URI S3, la vectoriser en dimension 512, et persister son index dans Qdrant de manière isolée,
So that le système soit hautement distribué par doigt, libérant le hub central.

**Acceptance Criteria:**

**Given** une notification de routage d'orchestrateur (`afis-master`) envoyant une action sur 1 doigt
**When** le worker télécharge le blob et l'analyse via l'infrastructure `FingerprintVectorizer` protégée
**Then** Qdrant indexe un ID global avec un payload pointant sur son fameux "RID" original
**And** les ressources C++ `try-with-resources` sont nettoyées immédiatement pour garantir le seuil mémoire.

## Epic 2: Pipeline de Déduplication Hybride Distribué

### Story 2.1: Recherche HNSW et Comparaison AFIS (Worker unitaire)

As a worker `afis-service`,
I want lancer la recherche spatiale de mon doigt, transformer l'image en `FingerprintTemplate` SourceAFIS et mener la comparaison strictement avec mon Top 100,
So that je retourne au Master une décision formelle et un template pré-compilé sans l'obliger à décoder d'image.

**Acceptance Criteria:**

**Given** un scan individuel vectorisé avec succès
**When** la requête gRPC HNSW remonte le top 100 suspect
**Then** `afis-service` génère l'objet métier de SourceAFIS et s'assure par une stricte routine déterministe s'il identifie une correspondance
**And** il poste un message de retour "statut validé" orienté `afis-master` contenant sa conclusion unitaire et son template compilé.

### Story 2.2: Orchestration, Consolidation et Archivage (afis-master)

As a orchestrateur central (`afis-master`),
I want rassembler les verdicts isolés de l'Epic 2.1 de chaque doigt, et prononcer une consigne métier finale par "individu" (RID),
So that la base DB et le cluster S3 soient finalisés sans états pendants.

**Acceptance Criteria:**

**Given** la conclusion du balayage de tous les sous-messages d'un RID ciblé
**When** l'état métier remonte un "Unique" (Pas de doublon avéré)
**Then** le master déplace programmatiquement tous les objets du dossier S3 `queue-processing/{rid}/*` vers le seau définitif `store/{rid}/*`
**And** MongoDB ingère exclusivement le `FingerprintTemplate` natif renvoyé par le broker, purgeant à jamais l'intégration base64
**And** _Given_ le statut remonte un "Doublon" avéré, _Then_ l'ensemble des blobs S3 ciblés sous `queue-processing` sont détruits.

## Epic 3: Observabilité et Télémétrie Opérationnelle

Les administrateurs peuvent diagnostiquer les performances asynchrones liées au nouveau pipeline hybride et analyser finement la réparition des temps d'ingestion/matching entre le ML (ONNX), Qdrant et SourceAFIS.

### Story 3.1: Implémentation des Timers Actuator et Logging

As a administrateur système,
I want monitorer la durée chronométrée séparément de chaque grande étape (Vectorisation ONNX, Requête HNSW Qdrant, Validation SourceAFIS),
So that je puisse isoler instantanément la cause racine d'un ralentissement lors d'un enrôlement de masse.

**Acceptance Criteria:**

**Given** un outil de monitoring (ex: Prometheus) branché sur le `/actuator/metrics` (déjà sécurisé Keycloak par le système)
**When** le pipeline hybride est sollicité massivement via des messages Kafka
**Then** le code applicatif remonte des chronométrages distincts (via Timers Micrometer) par étape métier clé (`afis.vectorize.time`, `afis.hnsw.time`, `afis.sourceafis.time`)
**And** si un profil biométrique de test ou de vérification manuelle passe par une invocation synchrone, la réponse `Response.builder()` renvoie un tableau contenant le détail des millisecondes consommées par étape.
