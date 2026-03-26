# LandReg Infrastructure as Code - Production

This directory contains Infrastructure as Code (IaC) configurations for deploying the LandReg & AFIS system.

## Deployment Options

You can choose between two deployment methods:

### 1. AWS EKS Deployment (`/aws`)
Full infrastructure on AWS EKS with Terraform.

### 2. On-Premises Deployment (`/on-prem`)
Deploy on any Kubernetes cluster (bare-metal, VM, on-prem).

---

## Quick Start - On-Premises (Recommended for local/dev)

If you have a Kubernetes cluster already running, or want to deploy locally:

```bash
# Install tools
cd on-prem/scripts
chmod +x bootstrap-server.sh
sudo ./bootstrap-server.sh

# Configure kubectl to point to your cluster
kubectl config use-context <your-cluster>

# Deploy LandReg
cd on-prem
./scripts/deploy.sh
```

## Quick Start - AWS EKS

```bash
cd aws
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars with your AWS settings
make plan
make apply
make deploy
```

## Directory Structure

```
deployments/prod/
├── aws/                    # AWS EKS deployment
│   ├── terraform/          # Terraform configuration
│   ├── main.tf
│   ├── variables.tf
│   └── outputs.tf
│
├── on-prem/               # On-premises deployment
│   ├── k8s/               # Kubernetes manifests
│   ├── helm-values/        # Helm chart values
│   ├── scripts/           # Deployment scripts
│   │   ├── bootstrap-server.sh
│   │   ├── bootstrap-macos.sh
│   │   └── deploy.sh
│   └── README.md
│
├── .github/workflows/      # CI/CD pipelines
├── Makefile               # Root Makefile
└── README.md              # This file
```

## Components (Same for both deployments)

- **PostgreSQL 14** - Primary database
- **MongoDB 5.0** - AFIS data storage
- **Apache Kafka 3.6** - Message queue
- **Redis 7.2** - Caching
- **Backend API** - Spring Boot application
- **AFIS Master** - Fingerprint management
- **AFIS Workers** - Scalable fingerprint processing
- **Monitoring** - Prometheus, Grafana, ELK stack

## Requirements

### For On-Premises
- Kubernetes 1.28+
- Helm 3.14+
- kubectl configured

### For AWS
- Terraform 1.6+
- AWS CLI configured
- kubectl, Helm, eksctl

## Documentation

| Document | Description |
|----------|-------------|
| [OPS-GUIDE.md](OPS-GUIDE.md) | Guide complet pour Ops - Installation, configuration, debugging |
| [QUICK-REFERENCE.md](QUICK-REFERENCE.md) | Référence rapide pour les commandes quotidiennes |

### OPS-GUIDE.md - Contenu

1. **Prérequis** - Matériel, logiciels, ports réseau
2. **Installation des outils** - Script automatique ou manuel
3. **Configuration de l'infrastructure** - Nodes, storage, passwords
4. **Déploiement** - Automatique et étape par étape
5. **Vérification post-déploiement** - Tests de santé
6. **Accès aux services** - URLs et credentials
7. **Guide d'exploitation Kubernetes** - Commandes, Grafana, Prometheus
8. **Troubleshooting** - Problèmes courants et solutions
9. **Checklist de déploiement** - Pour valider le déploiement

## License

Proprietary - LandReg
