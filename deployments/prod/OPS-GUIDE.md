# Guide Complet de Déploiement LandReg

Ce guide permet à tout Ops de déployer le système LandReg & AFIS from scratch sans aide externe.

---

## Table des Matières

1. [Prérequis](#1-prérequis)
2. [Installation des Outils](#2-installation-des-outils)
3. [Configuration de l'Infrastructure](#3-configuration-de-linfrastructure)
4. [Déploiement](#4-déploiement)
5. [Vérification Post-Déploiement](#5-vérification-post-déploiement)
6. [Accès aux Services](#6-accès-aux-services)
7. [Guide d'Exploitation Kubernetes](#7-guide-dexploitation-kubernetes)
8. [Troubleshooting](#8-troubleshooting)

---

## 1. Prérequis

### 1.1 Matériel Minimum Requis

| Composant | CPU | RAM | Stockage | Nombre |
|-----------|-----|-----|----------|--------|
| Master Node | 4 cores | 8 GB | 100 GB SSD | 1-3 |
| Database Node | 4 cores | 16 GB | 200 GB SSD | 2 |
| Middleware Node | 4 cores | 16 GB | 100 GB SSD | 2 |
| API Node | 4 cores | 8 GB | 50 GB | 2 |
| AFIS Master Node | 4 cores | 16 GB | 100 GB SSD | 2 |
| AFIS Worker Node | 8 cores | 32 GB | 100 GB SSD | 1-10 |
| Monitoring Node | 4 cores | 8 GB | 200 GB | 1 |

### 1.2 Logiciel Requis

- **OS**: Ubuntu 20.04+ / CentOS 8+ / RHEL 8+
- **Kubernetes**: 1.28+
- **kubectl**: 1.28+
- **Helm**: 3.14+
- **Docker**: 24+ (pour build local)
- **jq**: pour parser JSON
- **curl/wget**: pour téléchargements

### 1.3 Ports à Ouvrir

```
Master Node:
  - 6443: API Server
  - 2379-2380: etcd
  - 10250: Kubelet
  - 10251: kube-scheduler
  - 10252: kube-controller-manager

Worker Nodes:
  - 10250: Kubelet
  - 30000-32767: NodePort services

Load Balancer (optionnel):
  - 80, 443: HTTP/HTTPS
```

---

## 2. Installation des Outils

### 2.1 Option A: Script Automatique (Recommandé)

```bash
cd deployments/prod/on-prem/scripts

# Rendre exécutable
chmod +x bootstrap-server.sh

# Lancer l'installation complète
sudo ./bootstrap-server.sh
```

### 2.2 Option B: Installation Manuelle

```bash
# Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# kubectl
curl -LO "https://dl.k8s.io/release/v1.28.0/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Helm
curl -fsSL https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Kubernetes (kubeadm) - Sur le master uniquement
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.28/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.28/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt-get update
sudo apt-get install -y kubeadm kubelet kubectl
```

---

## 3. Configuration de l'Infrastructure

### 3.0 Composants Par Défaut (Aucun Choix Requis)

Tous les composants suivants sont **installés automatiquement** par le script `deploy.sh`. Aucune configuration n'est requise pour un déploiement standard.

| Composant | Default | Version | Rôle |
|-----------|---------|---------|------|
| **Storage** | local-path-provisioner | latest | Stockage persistant sur disque local |
| **LoadBalancer** | MetalLB | v0.13 | IP virtuelle pour services LoadBalancer |
| **Ingress** | Nginx Ingress | latest | Routage HTTP/HTTPS |
| **Logging** | Loki | latest | Agrégation des logs |
| **Monitoring** | Prometheus + Grafana | latest | Métriques et dashboards |

**Alternatives disponibles** (uniquement si vous avez une infrastructure existante):
- Storage: NFS, Ceph, Longhorn
- Ingress: Traefik, Ambassador

### 3.1 Cluster Kubernetes - Méthode kubeadm

#### Sur le Master Node:

```bash
# Initialiser le cluster
sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --service-cidr=10.96.0.0/12

# Configurer kubectl pour l'utilisateur courant
mkdir -p $HOME/.kube
sudo cp /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

# Installer le réseau (Calico)
kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml

# Vérifier le status
kubectl get nodes
kubectl get pods -n kube-system
```

#### Sur chaque Worker Node:

```bash
# Joindre au cluster (copier la commande depuis kubeadm init)
sudo kubeadm join <MASTER_IP>:6443 --token <TOKEN> --discovery-token-ca-cert-hash sha256:<HASH>
```

### 3.2 Variables à Configurer

#### 3.2.1 Configuration des Nodes

Éditer `on-prem/k8s/nodes-config.yaml` (à créer):

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: node-config
  namespace: default
data:
  # Labels pour sélection des nodes
  database-label: "node-type=database"
  middleware-label: "node-type=middleware"
  api-label: "node-type=api"
  afis-label: "node-type=afis"
  monitoring-label: "node-type=monitoring"
```

Appliquer les labels aux nodes:

```bash
# Lister les nodes
kubectl get nodes --show-labels

# Appliquer les labels (adapter selon vos nodes)
kubectl label node <NODE_NAME> node-type=database
kubectl label node <NODE_NAME> node-type=middleware
kubectl label node <NODE_NAME> node-type=api
kubectl label node <NODE_NAME> node-type=afis-master
kubectl label node <NODE_NAME> node-type=afis-worker
kubectl label node <NODE_NAME> node-type=monitoring
```

#### 3.2.2 Configuration du Storage

**DEFAULT: local-path-provisioner** (installé automatiquement)

Le storage `local-path` est installé par défaut et fonctionne sans configuration supplémentaire. Les données sont stockées sur le disque local des nodes.

**Alternatives (si vous avez une infrastructure existante):**

Pour NFS:
```bash
helm repo add sig-storage https://sig-storage.github.io/charts
helm install nfs-client-provisioner sig-storage/nfs-subdir-external-provisioner \
  --set nfs.server=<NFS_SERVER_IP> \
  --set nfs.path=/exports \
  --namespace kube-system
```

Pour Ceph:
```bash
kubectl apply -f https://raw.githubusercontent.com/rook/rook/master/cluster/examples/kubernetes/ceph/common.yaml
kubectl apply -f https://raw.githubusercontent.com/rook/rook/master/cluster/examples/kubernetes/ceph/operator.yaml
kubectl apply -f https://raw.githubusercontent.com/rook/rook/master/cluster/examples/kubernetes/ceph/cluster.yaml
```

**Note:** Si vous utilisez une alternative, modifiez `on-prem/k8s/storage-class.yaml` et les `helm-values/*.yaml` pour utiliser le nom de votre storage class.

#### 3.2.3 Configuration des Ressources

Modifier les limits selon vos ressources disponibles dans `helm-values/*.yaml`:

```yaml
# Exemple: postgresql-values.yaml
resources:
  requests:
    memory: "4Gi"
    cpu: "1000m"
  limits:
    memory: "8Gi"
    cpu: "2000m"
```

### 3.3 Configuration desMots de Passe

```bash
# Créer un fichier de secrets
cat > .env << 'EOF'
export POSTGRES_PASSWORD=$(openssl rand -base64 32)
export MONGODB_PASSWORD=$(openssl rand -base64 32)
export ADMIN_PASSWORD=YourSecurePassword123!
export GRAFANA_PASSWORD=YourGrafanaPassword123!
EOF

# Charger les variables
source .env
```

---

## 4. Déploiement

### 4.0 Déploiement en 3 Étapes (Recommandé)

Le déploiement est simplifié au maximum. Tout est automatique.

```bash
# Étape 1: Installer les outils sur le serveur maître
cd deployments/prod/on-prem/scripts
chmod +x bootstrap-server.sh
sudo ./bootstrap-server.sh

# Étape 2: Initialiser le cluster Kubernetes
sudo kubeadm init --pod-network-cidr=10.244.0.0/16

# Configurer kubectl
mkdir -p $HOME/.kube
sudo cp /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

# Installer le réseau (Calico)
kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml

# Étape 3: Labelliser les nodes et déployer
cd ../..
chmod +x scripts/setup-nodes.sh
./scripts/setup-nodes.sh

# Lancer le déploiement
./scripts/deploy.sh
```

### 4.1 Déploiement Automatique Complet

Le script `deploy.sh` installe automatiquement:
- Nginx Ingress Controller (si absent)
- MetalLB (si absent)
- Local Path Provisioner (si absent)
- Tous les composants LandReg

```bash
cd deployments/prod/on-prem

# Variables optionnelles (par défaut: auto-généré)
export FINGERPRINT_COUNT=60000
export POSTGRES_PASSWORD="YourSecurePassword"   # Optionnel
export MONGODB_PASSWORD="YourMongoPassword"     # Optionnel

./scripts/deploy.sh
```

### 4.2 Déploiement Manuel Étape par Étape

```bash
# 1. Créer les namespaces
kubectl apply -f k8s/namespaces.yaml

# 2. Storage (déjà installé par deploy.sh si nécessaire)
kubectl apply -f k8s/storage-class.yaml

# 3. ConfigMaps et Secrets
kubectl apply -f k8s/configmaps.yaml

# 4. PostgreSQL
helm upgrade --install postgres bitnami/postgresql \
  --namespace database \
  --values helm-values/postgresql-values.yaml \
  --wait --timeout 15m

# 5. MongoDB
helm upgrade --install mongodb bitnami/mongodb \
  --namespace database \
  --values helm-values/mongodb-values.yaml \
  --wait --timeout 15m

# 6. Kafka
helm upgrade --install kafka bitnami/kafka \
  --namespace middleware \
  --values helm-values/kafka-values.yaml \
  --wait --timeout 15m

# 7. Redis
helm upgrade --install redis bitnami/redis \
  --namespace middleware \
  --values helm-values/redis-values.yaml \
  --wait --timeout 10m

# 8. Monitoring
helm upgrade --install prometheus prometheus-community/prometheus \
  --namespace monitoring \
  --values helm-values/prometheus-values.yaml \
  --wait --timeout 10m

helm upgrade --install grafana grafana/grafana \
  --namespace monitoring \
  --values helm-values/grafana-values.yaml \
  --wait --timeout 10m

helm upgrade --install elasticsearch elastic/elasticsearch \
  --namespace monitoring \
  --values helm-values/elasticsearch-values.yaml \
  --wait --timeout 15m

helm upgrade --install kibana elastic/kibana \
  --namespace monitoring \
  --values helm-values/kibana-values.yaml \
  --wait --timeout 10m

# 9. Applications
kubectl apply -f k8s/backend-api-deployment.yaml
kubectl apply -f k8s/afis-master-deployment.yaml
kubectl apply -f k8s/afis-worker-deployment.yaml
```

---

## 5. Vérification Post-Déploiement

### 5.1 Vérification des Pods

```bash
# Tous les pods doivent être Running ou Completed
kubectl get pods --all-namespaces

# pods database
kubectl get pods -n database

# pods middleware
kubectl get pods -n middleware

# pods backend
kubectl get pods -n backend

# pods afis
kubectl get pods -n afis

# pods monitoring
kubectl get pods -n monitoring
```

**Statut attendu:**
```
NAME                                READY   STATUS    RESTARTS   AGE
postgres-postgresql-0               1/1     Running   0          5m
postgres-postgresql-1               1/1     Running   0          5m
mongodb-0                          1/1     Running   0          5m
mongodb-1                          1/1     Running   0          5m
kafka-0                            1/1     Running   0          5m
kafka-1                            1/1     Running   0          5m
kafka-2                            1/1     Running   0          5m
redis-master-0                     1/1     Running   0          3m
redis-replica-0                    1/1     Running   0          3m
redis-replica-1                    1/1     Running   0          3m
landreg-api-abc123                1/1     Running   0          2m
landreg-api-def456                1/1     Running   0          2m
afis-master-xyz789                1/1     Running   0          2m
afis-master-uvw012                1/1     Running   0          2m
afis-worker-123abc                1/1     Running   0          2m
prometheus-server-456def           1/1     Running   0          3m
grafana-789ghi                    1/1     Running   0          3m
```

### 5.2 Vérification des Services

```bash
kubectl get services --all-namespaces

# Test de connectivité
kubectl run -it --rm debug --image=busybox --restart=Never -- nslookup kubernetes.default
kubectl run -it --rm debug --image=busybox --restart=Never -- nc -zv postgres-postgresql.database.svc.cluster.local 5432
```

### 5.3 Vérification des Deployments

```bash
kubectl get deployments --all-namespaces

# Vérifier les replicas
kubectl rollout status deployment/landreg-api -n backend
kubectl rollout status deployment/afis-master -n afis
kubectl rollout status deployment/afis-worker -n afis
```

### 5.4 Vérification du Storage

```bash
kubectl get pvc --all-namespaces

# Statut attendu: Bound
NAME                      STATUS   VOLUME                                     CAPACITY
postgres-data-postgres-0  Bound    pvc-xxxx                                  100Gi
mongodb-data-mongodb-0    Bound    pvc-yyyy                                  100Gi
kafka-data-kafka-0        Bound    pvc-zzzz                                  50Gi
```

### 5.5 Tests de Santé

```bash
# API Backend
kubectl port-forward svc/landreg-api-service 8080:8080 -n backend &
curl -s http://localhost:8080/actuator/health

# AFIS Master
kubectl port-forward svc/afis-master-service 8081:8081 -n afis &
curl -s http://localhost:8081/actuator/health

# Prometheus
kubectl port-forward svc/prometheus-server 9090:80 -n monitoring &
curl -s http://localhost:9090/-/healthy

# Grafana
kubectl port-forward svc/grafana 3000:80 -n monitoring &
curl -s http://localhost:3000/api/health
```

---

## 6. Accès aux Services

### 6.1 Ports à Mapper

| Service | Port Local | Commande |
|---------|-----------|----------|
| Backend API | 8080 | `kubectl port-forward -n backend svc/landreg-api-service 8080:8080` |
| AFIS Master | 8081 | `kubectl port-forward -n afis svc/afis-master-service 8081:8081` |
| AFIS Worker | 8082 | `kubectl port-forward -n afis svc/afis-worker-service 8082:8082` |
| Prometheus | 9090 | `kubectl port-forward -n monitoring svc/prometheus-server 9090:80` |
| Grafana | 3000 | `kubectl port-forward -n monitoring svc/grafana 3000:80` |
| Kibana | 5601 | `kubectl port-forward -n monitoring svc/kibana 5601:5601` |

### 6.2 Récupérer les Mots de Passe

```bash
# PostgreSQL
kubectl get secret postgres-postgresql -n database -o jsonpath='{.data.postgresql-password}' | base64 -d

# MongoDB
kubectl get secret mongodb -n database -o jsonpath='{.data.mongodb-password}' | base64 -d

# Grafana
kubectl get secret grafana -n monitoring -o jsonpath='{.data.admin-password}' | base64 -d

# Kafka
kubectl get secret kafka -n middleware -o jsonpath='{.data.kafka-passwords}' | base64 -d
```

### 6.3 URLs et Credentials par Défaut

**Via port-forward (sans Ingress):**
```bash
kubectl port-forward svc/grafana 3000:80 -n monitoring
# Accès: http://localhost:3000 (admin/admin par défaut)
```

**Via Ingress (après configuration DNS local):**

Ajouter dans `/etc/hosts`:
```
<METALLB_IP> api.landreg.local grafana.landreg.local prometheus.landreg.local kibana.landreg.local
```

| Service | URL | Username | Password |
|---------|-----|----------|----------|
| Grafana | http://grafana.landreg.local | admin | `echo $ADMIN_PASSWORD` ou `admin` |
| Prometheus | http://prometheus.landreg.local | - | - |
| Kibana | http://kibana.landreg.local | - | - |
| API | http://api.landreg.local | - | - |

**Récupérer IP MetalLB:**
```bash
kubectl get svc -n ingress-nginx
# L'IP externe du service ingress-nginx-controller est l'IP à utiliser
```

### 6.4 Commandes Rapides d'Accès

```bash
# Monitoring Dashboard
kubectl port-forward -n monitoring svc/grafana 3000:80

# Prometheus
kubectl port-forward -n monitoring svc/prometheus-server 9090:80

# Kibana
kubectl port-forward -n monitoring svc/kibana 5601:5601

# API Backend
kubectl port-forward -n backend svc/landreg-api-service 8080:8080

# AFIS Master
kubectl port-forward -n afis svc/afis-master-service 8081:8081
```

---

## 7. Guide d'Exploitation Kubernetes

### 7.1 Commandes Essentielles

```bash
# ============================================
# VUE D'ENSEMBLE
# ============================================

# Lister tous les ressources
kubectl get all --all-namespaces

# Lister les nodes
kubectl get nodes -o wide

# Lister les pods avec détails
kubectl get pods -A -o wide

# Statut des nodes
kubectl top nodes

# ============================================
# LOGS ET DEBUG
# ============================================

# Logs d'un pod
kubectl logs <POD_NAME> -n <NAMESPACE>

# Logs en temps réel
kubectl logs -f <POD_NAME> -n <NAMESPACE>

# Logs de tous les containers
kubectl logs <POD_NAME> -n <NAMESPACE> --all-containers=true

# Logs précédentes (si restart)
kubectl logs --previous <POD_NAME> -n <NAMESPACE>

# Description détaillée
kubectl describe pod <POD_NAME> -n <NAMESPACE>

# Exécuter un shell dans un pod
kubectl exec -it <POD_NAME> -n <NAMESPACE> -- /bin/bash

# ============================================
# SCALING
# ============================================

# Scale un deployment
kubectl scale deployment landreg-api --replicas=3 -n backend

# Scale AFIS workers
kubectl scale deployment afis-worker --replicas=5 -n afis

# ============================================
# ROLLBACK
# ============================================

# Voir l'historique
kubectl rollout history deployment/landreg-api -n backend

# Rollback
kubectl rollout undo deployment/landreg-api -n backend

# Rollback vers une version spécifique
kubectl rollout undo deployment/landreg-api --to-revision=2 -n backend

# ============================================
# RESEAUX
# ============================================

# Lister les services
kubectl get svc -A

# Lister les endpoints
kubectl get endpoints -A

# Lister les ingress
kubectl get ingress -A

# DNS lookup
kubectl run -it --rm dnsutils --image=tutum/dnsutils --restart=Never -- nslookup postgres-postgresql.database.svc.cluster.local

# Test de connectivité
kubectl run -it --rm --image=busybox:1.28 shell --restart=Never -- wget -qO- http://landreg-api-service.backend:8080/actuator/health

# ============================================
# RESOURCES
# ============================================

# Utilisation CPU/RAM par pod
kubectl top pods -A

# Utilisation par namespace
kubectl top pods -n <NAMESPACE>

# Limites et requests d'un pod
kubectl get pod <POD_NAME> -n <NAMESPACE> -o jsonpath='{.spec.containers[*].resources}'
```

### 7.2 Surveillance Continue

```bash
# Watch sur les pods
watch -n 5 kubectl get pods -A

# Watch sur les events
kubectl get events -A --sort-by='.lastTimestamp'

# Dashboard kubectl
kubectl proxy

# Accéder au dashboard (si installé)
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
kubectl proxy
# Ouvrir: http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
```

### 7.3 Exploration des Ressources

```bash
# ============================================
# NAMESPACES
# ============================================
kubectl get namespaces
kubectl describe namespace database

# ============================================
# CONFIGMAPS
# ============================================
kubectl get configmap -n backend
kubectl get configmap database-config -n backend -o yaml

# ============================================
# SECRETS
# ============================================
kubectl get secrets -n database
kubectl get secret db-secrets -n database -o yaml

# ============================================
# SERVICES
# ============================================
kubectl get svc -n backend -o wide
kubectl describe svc landreg-api-service -n backend

# ============================================
# PERSISTENT VOLUMES
# ============================================
kubectl get pv
kubectl get pvc -A
kubectl describe pvc postgres-data-postgres-0 -n database

# ============================================
# INGRESS (si configuré)
# ============================================
kubectl get ingress -A
kubectl describe ingress <INGRESS_NAME> -n backend
```

### 7.4 Maintenance

```bash
# ============================================
# DRAIN A NODE (avant maintenance)
# ============================================
kubectl drain <NODE_NAME> --ignore-daemonsets --delete-emptydir-data

# Remettre le node en service
kubectl uncordon <NODE_NAME>

# ============================================
# CORDON (exclure des nouveaux pods)
# ============================================
kubectl cordon <NODE_NAME>

# ============================================
# RESTART PODS
# ============================================
# Par delete
kubectl delete pod <POD_NAME> -n <NAMESPACE>

# Par deployment (plus propre)
kubectl rollout restart deployment/landreg-api -n backend

# ============================================
# BACKUP/RESTORE
# ============================================
# PostgreSQL backup
kubectl exec -it postgres-postgresql-0 -n database -- pg_dump -U postgres landreg > backup.sql

# MongoDB backup
kubectl exec -it mongodb-0 -n database -- mongodump --archive=/tmp/backup.archive --db=landreg_afis
kubectl cp database/mongodb-0:/tmp/backup.archive ./mongodb-backup.archive
```

### 7.5 Dashboard Grafana - Guide Rapide

```bash
# Accéder
kubectl port-forward svc/grafana 3000:80 -n monitoring

# Login: admin / <ADMIN_PASSWORD>

# Dashboards Pré-configurés:
# - JVM Micrometer (métriques Spring Boot)
# - Kafka Dashboard
# - PostgreSQL Dashboard
# - MongoDB Dashboard
# - Kubernetes Pods Overview

# Créer un Dashboard Custom:
1. Menu "+" -> Create -> Dashboard
2. Add new panel
3. Query: PromQL queries (ex: rate(http_requests_total[5m]))
4. Visualization: Graph/Stat/Pie chart
5. Save

# Queries Utiles:
# - Taux d'erreur: rate(http_server_requests_seconds_count{status=~"5.."}[5m])
# - Latence P99: histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m]))
# - CPU Usage: rate(container_cpu_usage_seconds_total[5m])
# - Memory Usage: container_memory_usage_bytes / container_spec_memory_limit_bytes
```

### 7.6 Prometheus - Requêtes Courantes

```bash
# Accéder
kubectl port-forward svc/prometheus-server 9090:80 -n monitoring

# Requêtes populares:
# - Top 10 pods par CPU
topk(10, sum by(pod_name)(rate(container_cpu_usage_seconds_total[5m])))

# - Top 10 pods par RAM
topk(10, sum by(pod_name)(container_memory_usage_bytes))

# - Disponibilité services
up{job="kubernetes-service-endpoints"}

# - Errors rate
rate(http_requests_total{status=~"5.."}[5m])

# - AFIS Processing rate
rate(afis_fingerprint_processed_total[5m])
```

---

## 8. Troubleshooting

### 8.1 Problèmes Courants

#### Pod en CrashLoopBackOff
```bash
kubectl describe pod <POD_NAME> -n <NAMESPACE>
kubectl logs <POD_NAME> -n <NAMESPACE> --previous

# Causes fréquentes:
# - Configuration erronée (env vars)
# - Liveness probe failure
# - OOM Kill (Out of Memory)
```

#### Pod en Pending
```bash
kubectl describe pod <POD_NAME> -n <NAMESPACE>

# Causes:
# - Ressources insuffisantes
# - Pas de node correspondant au nodeSelector
# - PVC non provisionné

# Solutions:
kubectl label node <NODE> node-type=<TYPE>
kubectl patch storageclass standard -p '{"metadata":{"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'
```

#### Volume non mountable
```bash
kubectl get events -n <NAMESPACE> --field-selector involvedObject.name=<PVC_NAME>

# Vérifier le provisionneur
kubectl get storageclass
kubectl describe storageclass <CLASS_NAME>
```

#### Image Pull Error
```bash
kubectl describe pod <POD_NAME> -n <NAMESPACE>

# Solution: Vérifier le registry et les credentials
kubectl create secret docker-registry regcred \
  --docker-server=<DOCKER_REGISTRY> \
  --docker-username=<USERNAME> \
  --docker-password=<PASSWORD> \
  --docker-email=<EMAIL> \
  -n <NAMESPACE>

# Ajouter au service account
kubectl patch serviceaccount default -p '{"imagePullSecrets":[{"name":"regcred"}]}' -n <NAMESPACE>
```

#### Connection Refused - Services
```bash
# Vérifier les endpoints
kubectl get endpoints <SERVICE_NAME> -n <NAMESPACE>

# Vérifier les selectors
kubectl describe svc <SERVICE_NAME> -n <NAMESPACE>

# Les endpoints doivent correspondre aux pods
kubectl get pods -n <NAMESPACE> -l app=<APP_LABEL>
```

### 8.2 Commands de Diagnostic

```bash
# Events récents
kubectl get events -A --sort-by='.lastTimestamp' | tail -50

# Resources avec problèmes
kubectl get pods -A | grep -E "Error|Evicted|Pending|ImagePullBackOff"

# Nodes non prêts
kubectl get nodes | grep -v Ready

# PersistentVolume claims non bound
kubectl get pvc -A | grep -v Bound

# Logs errors
kubectl logs -f -n <NAMESPACE> -l app=<APP> --tail=100 2>&1 | grep -i error

# Network policies
kubectl get networkpolicies -A

# Service mesh status (si Istio)
kubectl get pods -n istio-system
```

### 8.3 Procedures de Recovery

#### Recovery d'un Pod
```bash
# Via delete (recreation automatique)
kubectl delete pod <POD_NAME> -n <NAMESPACE>

# Via rollout restart (plus propre)
kubectl rollout restart deployment/<DEPLOYMENT_NAME> -n <NAMESPACE>

# Rollback si problème après restart
kubectl rollout undo deployment/<DEPLOYMENT_NAME> -n <NAMESPACE>
```

#### Recovery d'une Database
```bash
# PostgreSQL
kubectl exec -it postgres-postgresql-0 -n database -- psql -U postgres

# Restore backup
cat backup.sql | kubectl exec -i postgres-postgresql-0 -n database -- psql -U postgres landreg

# MongoDB
kubectl exec -it mongodb-0 -n database -- mongosh

# Restore backup
kubectl exec -it mongodb-0 -n database -- mongorestore --archive=/tmp/backup.archive --db=landreg_afis
```

#### Full Reset du Cluster
```bash
# WARNING: Supprime TOUT

# Supprimer les namespaces
kubectl delete namespace database middleware backend afis monitoring

# Supprimer les helm releases
helm ls --all-namespaces
helm uninstall <RELEASE> -n <NAMESPACE>

# Supprimer les CRDs
kubectl get crds -o name | xargs kubectl delete

# Re-déployer
./scripts/deploy.sh
```

---

## 9. Checklist de Déploiement

**Étape 1: Infrastructure**
- [ ] Serveurs avec OS installé (Ubuntu 20.04+ / CentOS 8+)
- [ ] Ports réseau ouverts (6443, 10250, 30000-32767)
- [ ] Script bootstrap exécuté: `sudo ./bootstrap-server.sh`

**Étape 2: Cluster Kubernetes**
- [ ] Cluster initialisé: `kubeadm init`
- [ ] kubectl configuré: `~/.kube/config`
- [ ] Network plugin installé (Calico)
- [ ] Worker nodes join au cluster

**Étape 3: Labels Nodes**
- [ ] Labels appliqués via `./setup-nodes.sh`
- [ ] Vérification: `kubectl get nodes --show-labels | grep node-type`

**Étape 4: Déploiement**
- [ ] Script exécuté: `./deploy.sh`
- [ ] Tous les pods en Running: `kubectl get pods -A`
- [ ] PVCs bound: `kubectl get pvc -A`

**Étape 5: Vérification**
- [ ] API healthy: `curl http://localhost:8080/actuator/health`
- [ ] AFIS healthy: `curl http://localhost:8081/actuator/health`
- [ ] Grafana accessible: `kubectl port-forward svc/grafana 3000:80`
- [ ] Prometheus accessible: `kubectl port-forward svc/prometheus-server 9090:80`

---

## 10. Contacts et Escalation

| Rôle | Contact |
|------|---------|
| DevOps Lead | (à configurer) |
| Database Admin | (à configurer) |
| Platform Team | (à configurer) |

**URLs Importantes:**
- Documentation: `/deployments/prod/OPS-GUIDE.md`
- Monitoring: MetalLB IP (voir `kubectl get svc -n ingress-nginx`)

---

*Document mis à jour le: 2026-03-20*
*Version: 2.0*

*Document mis à jour le: $(date +%Y-%m-%d)*
*Version: 1.0*
