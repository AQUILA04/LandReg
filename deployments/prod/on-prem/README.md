# LandReg - On-Premises Deployment

This directory contains Kubernetes manifests and Helm values for deploying LandReg on any Kubernetes cluster (bare-metal, VM, or on-premises).

## Prerequisites

- Kubernetes 1.28+
- Helm 3.14+
- kubectl configured to point to your cluster
- At least 3 nodes with:
  - 8GB+ RAM (database nodes)
  - 16GB+ RAM (middleware, AFIS nodes)

## Quick Start

### 1. Bootstrap Server (if starting from scratch)

```bash
cd scripts
chmod +x bootstrap-server.sh
sudo ./bootstrap-server.sh
```

### 2. Connect to Your Kubernetes Cluster

```bash
# For existing cluster
kubectl config use-context <your-context>

# Verify connection
kubectl get nodes
```

### 3. Label Your Nodes

```bash
# Option A: Script interactif (recommandé)
chmod +x scripts/setup-nodes.sh
./scripts/setup-nodes.sh

# Option B: Manuel
kubectl label node <NODE_NAME> node-type=database
kubectl label node <NODE_NAME> node-type=middleware
kubectl label node <NODE_NAME> node-type=api
kubectl label node <NODE_NAME> node-type=afis-master
kubectl label node <NODE_NAME> node-type=afis-worker
kubectl label node <NODE_NAME> node-type=monitoring
```

### 4. Configure Infrastructure

Edit `k8s/nodes-config.yaml` with your infrastructure details:
- Node names/IPs
- Storage configuration (NFS, local, etc.)
- Network settings

### 5. Deploy LandReg

```bash
./scripts/deploy.sh
```

## Directory Structure

```
on-prem/
├── k8s/                  # Kubernetes manifests
│   ├── namespaces.yaml    # Namespace definitions
│   ├── storage-class.yaml # Storage configuration
│   ├── nodes-config.yaml  # Node/infrastructure config (CUSTOMIZE!)
│   ├── configmaps.yaml   # ConfigMaps & Secrets
│   ├── backend-api-deployment.yaml
│   ├── afis-master-deployment.yaml
│   ├── afis-worker-deployment.yaml
│   └── monitoring-rbac.yaml
│
├── helm-values/          # Helm chart values
│   ├── postgresql-values.yaml
│   ├── mongodb-values.yaml
│   ├── kafka-values.yaml
│   ├── redis-values.yaml
│   ├── prometheus-values.yaml
│   ├── grafana-values.yaml
│   ├── elasticsearch-values.yaml
│   └── kibana-values.yaml
│
├── scripts/              # Deployment scripts
│   ├── bootstrap-server.sh    # Install tools (Linux)
│   ├── bootstrap-macos.sh      # Install tools (macOS)
│   ├── setup-nodes.sh          # Interactive node labeling
│   ├── deploy.sh               # Main deployment script
│   └── terraform-*.sh          # Terraform scripts (optional)
│
└── README.md            # This file
```

## Deployment Order

The deployment script automatically handles the correct order:

1. Check prerequisites (kubectl, helm, cluster connection)
2. Create namespaces
3. Node configuration (manual - see nodes-config.yaml)
4. Configure storage classes
5. Deploy databases (PostgreSQL, MongoDB)
6. Deploy middleware (Kafka, Redis)
7. Deploy monitoring stack
6. Deploy applications

## Manual Deployment

If you prefer manual control:

```bash
# Create namespaces
kubectl apply -f k8s/namespaces.yaml

# Deploy databases
helm repo add bitnami https://charts.bitnami.com/bitnami
helm upgrade --install postgres bitnami/postgresql --namespace database --values helm-values/postgresql-values.yaml --wait

helm upgrade --install mongodb bitnami/mongodb --namespace database --values helm-values/mongodb-values.yaml --wait

# Deploy middleware
helm upgrade --install kafka bitnami/kafka --namespace middleware --values helm-values/kafka-values.yaml --wait

helm upgrade --install redis bitnami/redis --namespace middleware --values helm-values/redis-values.yaml --wait

# Deploy applications
kubectl apply -f k8s/

# Check status
kubectl get pods --all-namespaces
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| FINGERPRINT_COUNT | Total fingerprints | 60000 |
| POSTGRES_PASSWORD | PostgreSQL password | auto-generated |
| MONGODB_PASSWORD | MongoDB password | auto-generated |
| ADMIN_PASSWORD | Grafana admin password | admin |

### AFIS Workers Scaling

Workers are automatically calculated: `1 worker per 20,000 fingerprints`

For 60,000 fingerprints = 3 workers minimum.

## Storage

The deployment uses `gp3` storage class by default. For on-premises, you may need to:

1. Create a custom StorageClass for your storage backend
2. Update `k8s/storage-class.yaml` with your provisioner
3. Update Helm values with your StorageClass name

## Monitoring

After deployment, access monitoring services:

```bash
# Prometheus
kubectl port-forward -n monitoring svc/prometheus-server 9090:80

# Grafana
kubectl port-forward -n monitoring svc/grafana 3000:80

# Kibana
kubectl port-forward -n monitoring svc/kibana 5601:5601
```

## Troubleshooting

```bash
# Check all pods
kubectl get pods --all-namespaces

# Check specific pod logs
kubectl logs -n <namespace> <pod-name>

# Describe pod for issues
kubectl describe pod -n <namespace> <pod-name>

# Check pod events
kubectl get events --all-namespaces --sort-by='.lastTimestamp'
```

## Uninstall

```bash
# Remove all LandReg resources
kubectl delete namespace database middleware backend afis monitoring

# Remove Helm releases
helm ls --all-namespaces | awk '{print $1}' | xargs -I {} helm uninstall {} --namespace {}
```

## License

Proprietary - LandReg
