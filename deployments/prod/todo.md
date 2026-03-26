A comprehensive Infrastructure as Code (IaC) configuration to set up a Kubernetes cluster with all the nodes and services. I'll use a combination of Terraform for infrastructure provisioning and Kubernetes manifests/Helm charts for application deployment.

## Infrastructure as Code (IaC) for Kubernetes Cluster

### 1. **Terraform Configuration - Infrastructure Provisioning**

```hcl
# main.tf - Terraform configuration for Kubernetes cluster

terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.0"
    }
  }
}

# Configure AWS Provider (example with AWS - adapt to your cloud provider)
provider "aws" {
  region = var.aws_region
}

# VPC Configuration
module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  version = "5.0.0"

  name = "landreg-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["${var.aws_region}a", "${var.aws_region}b"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24"]

  enable_nat_gateway = true
  enable_vpn_gateway = false
  enable_dns_hostnames = true

  tags = {
    Environment = "production"
    Project     = "landreg-afis"
  }
}

# EKS Cluster
module "eks" {
  source = "terraform-aws-modules/eks/aws"
  version = "19.0.0"

  cluster_name    = "landreg-afis-cluster"
  cluster_version = "1.28"

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  # Node Groups
  eks_managed_node_groups = {
    # Database nodes (equivalent to Database Server)
    database = {
      desired_size = 2
      min_size     = 2
      max_size     = 4

      instance_types = ["t3.large"] # 8GB RAM equivalent
      capacity_type  = "ON_DEMAND"

      labels = {
        node-type = "database"
        tier      = "data"
      }

      tags = {
        Name = "database-node"
      }
    }

    # Middleware nodes (Kafka, Redis, Zookeeper)
    middleware = {
      desired_size = 2
      min_size     = 2
      max_size     = 3

      instance_types = ["t3.xlarge"] # 16GB RAM equivalent
      capacity_type  = "ON_DEMAND"

      labels = {
        node-type = "middleware"
        tier      = "middleware"
      }

      tags = {
        Name = "middleware-node"
      }
    }

    # API nodes (Backend API Server)
    api = {
      desired_size = 2
      min_size     = 2
      max_size     = 5

      instance_types = ["t3.large"] # 8GB RAM equivalent
      capacity_type  = "ON_DEMAND"

      labels = {
        node-type = "api"
        tier      = "application"
      }

      tags = {
        Name = "api-node"
      }
    }

    # AFIS Master nodes
    afis-master = {
      desired_size = 2
      min_size     = 2
      max_size     = 3

      instance_types = ["t3.xlarge"] # 16GB RAM equivalent
      capacity_type  = "ON_DEMAND"

      labels = {
        node-type = "afis-master"
        tier      = "processing"
      }

      tags = {
        Name = "afis-master-node"
      }
    }

    # AFIS Worker nodes (scalable based on fingerprint count)
    afis-workers = {
      desired_size = var.afis_worker_count
      min_size     = 1
      max_size     = 10

      instance_types = ["t3.xlarge"] # 16GB RAM equivalent
      capacity_type  = "ON_DEMAND"

      labels = {
        node-type = "afis-worker"
        tier      = "processing"
      }

      tags = {
        Name = "afis-worker-node"
      }
    }

    # Monitoring nodes
    monitoring = {
      desired_size = 1
      min_size     = 1
      max_size     = 2

      instance_types = ["t3.xlarge"] # 16GB RAM equivalent
      capacity_type  = "ON_DEMAND"

      labels = {
        node-type = "monitoring"
        tier      = "observability"
      }

      tags = {
        Name = "monitoring-node"
      }
    }
  }

  tags = {
    Environment = "production"
    Project     = "landreg-afis"
  }
}
```

### 2. **Variables Configuration**

```hcl
# variables.tf
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-3" # Paris region
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "production"
}

variable "afis_worker_count" {
  description = "Number of AFIS workers (based on fingerprint count)"
  type        = number
  default     = 3 # Adjust based on 20,000 fingerprints per worker
}

variable "fingerprint_count" {
  description = "Total number of fingerprints in the system"
  type        = number
  default     = 60000 # Example: 60,000 fingerprints -> 3 workers
}

# locals.tf
locals {
  required_workers = ceil(var.fingerprint_count / 20000)
  afis_worker_count = local.required_workers > var.afis_worker_count ? local.required_workers : var.afis_worker_count
}
```

### 3. **Kubernetes Namespaces Configuration**

```yaml
# namespaces.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: database
  labels:
    name: database
    tier: data
---
apiVersion: v1
kind: Namespace
metadata:
  name: middleware
  labels:
    name: middleware
    tier: middleware
---
apiVersion: v1
kind: Namespace
metadata:
  name: backend
  labels:
    name: backend
    tier: application
---
apiVersion: v1
kind: Namespace
metadata:
  name: afis
  labels:
    name: afis
    tier: processing
---
apiVersion: v1
kind: Namespace
metadata:
  name: monitoring
  labels:
    name: monitoring
    tier: observability
```

### 4. **Helm Charts Configuration**

```yaml
# helm-values.yaml

# PostgreSQL values
postgresql:
  namespace: database
  replicaCount: 2
  image:
    repository: postgres
    tag: 14
  persistence:
    size: 500Gi
    storageClass: gp2
  resources:
    requests:
      memory: "4Gi"
      cpu: "1000m"
    limits:
      memory: "8Gi"
      cpu: "2000m"
  postgresqlDatabase: landreg
  postgresqlUsername: landreg_user
  metrics:
    enabled: true
    serviceMonitor:
      enabled: true

# MongoDB values
mongodb:
  namespace: database
  replicaCount: 2
  image:
    repository: mongo
    tag: 5.0
  persistence:
    size: 500Gi
    storageClass: gp2
  resources:
    requests:
      memory: "4Gi"
      cpu: "1000m"
    limits:
      memory: "8Gi"
      cpu: "2000m"
  metrics:
    enabled: true
    serviceMonitor:
      enabled: true

# Kafka values
kafka:
  namespace: middleware
  replicaCount: 3
  image:
    repository: confluentinc/cp-kafka
    tag: 7.4.0
  persistence:
    size: 200Gi
    storageClass: gp2
  resources:
    requests:
      memory: "4Gi"
      cpu: "1000m"
    limits:
      memory: "8Gi"
      cpu: "2000m"
  metrics:
    enabled: true
    serviceMonitor:
      enabled: true

# Redis values
redis:
  namespace: middleware
  replicaCount: 2
  image:
    repository: redis
    tag: 7-alpine
  persistence:
    size: 50Gi
    storageClass: gp2
  resources:
    requests:
      memory: "2Gi"
      cpu: "500m"
    limits:
      memory: "4Gi"
      cpu: "1000m"
  metrics:
    enabled: true
    serviceMonitor:
      enabled: true
```

### 5. **Kubernetes Deployments**

```yaml
# backend-api-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: landreg-api
  namespace: backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: landreg-api
  template:
    metadata:
      labels:
        app: landreg-api
    spec:
      nodeSelector:
        node-type: api
      containers:
      - name: landreg-api
        image: landreg/optimize-land-reg:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: database-config
              key: postgres-host
        - name: DB_PORT
          value: "5432"
        - name: KAFKA_BOOTSTRAP_SERVERS
          valueFrom:
            configMapKeyRef:
              name: kafka-config
              key: bootstrap-servers
        - name: REDIS_HOST
          valueFrom:
            configMapKeyRef:
              name: redis-config
              key: host
        resources:
          requests:
            memory: "4Gi"
            cpu: "1000m"
          limits:
            memory: "8Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: landreg-api-service
  namespace: backend
spec:
  selector:
    app: landreg-api
  ports:
  - port: 8080
    targetPort: 8080
  type: LoadBalancer
```

```yaml
# afis-master-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: afis-master
  namespace: afis
spec:
  replicas: 2
  selector:
    matchLabels:
      app: afis-master
  template:
    metadata:
      labels:
        app: afis-master
    spec:
      nodeSelector:
        node-type: afis-master
      containers:
      - name: afis-master
        image: landreg/afis-master:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: MONGODB_HOST
          valueFrom:
            configMapKeyRef:
              name: mongodb-config
              key: host
        - name: MONGODB_PORT
          value: "27017"
        - name: KAFKA_BOOTSTRAP_SERVERS
          valueFrom:
            configMapKeyRef:
              name: kafka-config
              key: bootstrap-servers
        resources:
          requests:
            memory: "8Gi"
            cpu: "2000m"
          limits:
            memory: "16Gi"
            cpu: "4000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: afis-master-service
  namespace: afis
spec:
  selector:
    app: afis-master
  ports:
  - port: 8081
    targetPort: 8081
  type: ClusterIP
```

```yaml
# afis-worker-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: afis-worker
  namespace: afis
spec:
  replicas: ${AFIS_WORKER_COUNT} # Will be set by Helm
  selector:
    matchLabels:
      app: afis-worker
  template:
    metadata:
      labels:
        app: afis-worker
    spec:
      nodeSelector:
        node-type: afis-worker
      containers:
      - name: afis-worker
        image: landreg/afis-service:latest
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: KAFKA_BOOTSTRAP_SERVERS
          valueFrom:
            configMapKeyRef:
              name: kafka-config
              key: bootstrap-servers
        - name: REDIS_HOST
          valueFrom:
            configMapKeyRef:
              name: redis-config
              key: host
        - name: REDIS_PORT
          value: "6379"
        resources:
          requests:
            memory: "8Gi"
            cpu: "2000m"
          limits:
            memory: "16Gi"
            cpu: "4000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8082
          initialDelaySeconds: 60
          periodSeconds: 10
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: afis-worker-hpa
  namespace: afis
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: afis-worker
  minReplicas: 1
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### 6. **Monitoring Stack Configuration**

```yaml
# prometheus-values.yaml
prometheus:
  namespace: monitoring
  alertmanager:
    enabled: true
  pushgateway:
    enabled: false
  nodeExporter:
    enabled: true
  server:
    persistentVolume:
      enabled: true
      size: 100Gi
    resources:
      requests:
        memory: "4Gi"
        cpu: "1000m"
      limits:
        memory: "8Gi"
        cpu: "2000m"

# grafana-values.yaml
grafana:
  namespace: monitoring
  adminPassword: admin
  persistence:
    enabled: true
    size: 10Gi
  datasources:
    datasources.yaml:
      apiVersion: 1
      datasources:
      - name: Prometheus
        type: prometheus
        url: http://prometheus-server.monitoring.svc.cluster.local
        access: proxy
        isDefault: true
  dashboardProviders:
    dashboardproviders.yaml:
      apiVersion: 1
      providers:
      - name: 'default'
        orgId: 1
        folder: ''
        type: file
        disableDeletion: false
        editable: true
        options:
          path: /var/lib/grafana/dashboards/default
  dashboards:
    default:
      jvm-micrometer:
        url: https://grafana.com/api/dashboards/4701/revisions/1/download
      kafka:
        url: https://grafana.com/api/dashboards/7589/revisions/3/download
      postgresql:
        url: https://grafana.com/api/dashboards/9628/revisions/3/download
      mongodb:
        url: https://grafana.com/api/dashboards/2583/revisions/4/download

# elastic-stack-values.yaml
elasticsearch:
  namespace: monitoring
  replicas: 2
  minimumMasterNodes: 1
  resources:
    requests:
      memory: "4Gi"
      cpu: "1000m"
    limits:
      memory: "8Gi"
      cpu: "2000m"
  volumeClaimTemplate:
    accessModes: ["ReadWriteOnce"]
    resources:
      requests:
        storage: 100Gi

logstash:
  namespace: monitoring
  replicas: 1
  resources:
    requests:
      memory: "2Gi"
      cpu: "500m"
    limits:
      memory: "4Gi"
      cpu: "1000m"

kibana:
  namespace: monitoring
  replicas: 1
  resources:
    requests:
      memory: "1Gi"
      cpu: "500m"
    limits:
      memory: "2Gi"
      cpu: "1000m"
```

### 7. **ConfigMaps and Secrets**

```yaml
# configmaps.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: database-config
  namespace: backend
data:
  postgres-host: "postgres-postgresql.database.svc.cluster.local"
  mongodb-host: "mongodb.mongodb.database.svc.cluster.local"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: kafka-config
  namespace: backend
data:
  bootstrap-servers: "kafka.middleware.svc.cluster.local:9092"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: redis-config
  namespace: backend
data:
  host: "redis-master.middleware.svc.cluster.local"
  port: "6379"
---
apiVersion: v1
kind: Secret
metadata:
  name: db-secrets
  namespace: database
type: Opaque
data:
  postgres-password: <base64-encoded-password>
  mongodb-password: <base64-encoded-password>
```

### 8. **Deployment Script**

```bash
#!/bin/bash
# deploy.sh - Main deployment script

set -e

echo "🚀 Starting LandReg & AFIS System Deployment on Kubernetes"

# Create namespaces
echo "📁 Creating namespaces..."
kubectl apply -f k8s/namespaces.yaml

# Deploy databases
echo "💾 Deploying PostgreSQL..."
helm repo add bitnami https://charts.bitnami.com/bitnami
helm upgrade --install postgres bitnami/postgresql \
  --namespace database \
  --values helm-values/postgresql-values.yaml \
  --wait

echo "💾 Deploying MongoDB..."
helm upgrade --install mongodb bitnami/mongodb \
  --namespace database \
  --values helm-values/mongodb-values.yaml \
  --wait

# Deploy middleware
echo "🔄 Deploying Kafka..."
helm repo add confluentinc https://confluentinc.github.io/cp-helm-charts/
helm upgrade --install kafka confluentinc/cp-kafka \
  --namespace middleware \
  --values helm-values/kafka-values.yaml \
  --wait

echo "🔄 Deploying Redis..."
helm upgrade --install redis bitnami/redis \
  --namespace middleware \
  --values helm-values/redis-values.yaml \
  --wait

# Deploy monitoring stack
echo "📊 Deploying Prometheus & Grafana..."
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm upgrade --install prometheus prometheus-community/prometheus \
  --namespace monitoring \
  --values helm-values/prometheus-values.yaml \
  --wait

helm upgrade --install grafana grafana/grafana \
  --namespace monitoring \
  --values helm-values/grafana-values.yaml \
  --wait

echo "📊 Deploying ELK Stack..."
helm repo add elastic https://helm.elastic.co
helm upgrade --install elasticsearch elastic/elasticsearch \
  --namespace monitoring \
  --values helm-values/elasticsearch-values.yaml \
  --wait

helm upgrade --install kibana elastic/kibana \
  --namespace monitoring \
  --values helm-values/kibana-values.yaml \
  --wait

# Deploy application components
echo "🔧 Deploying Backend API..."
kubectl apply -f k8s/backend-api-deployment.yaml

echo "🔧 Deploying AFIS Master..."
kubectl apply -f k8s/afis-master-deployment.yaml

echo "🔧 Deploying AFIS Workers..."
# Calculate number of workers based on fingerprint count
export FINGERPRINT_COUNT=60000
export WORKER_COUNT=$(( ($FINGERPRINT_COUNT + 19999) / 20000 ))
echo "📊 Deploying $WORKER_COUNT AFIS Workers (1 per 20,000 fingerprints)"

cat k8s/afis-worker-deployment.yaml | sed "s/\${AFIS_WORKER_COUNT}/$WORKER_COUNT/g" | kubectl apply -f -

echo "✅ Deployment completed successfully!"

# Display deployment status
echo "📊 Deployment Status:"
kubectl get pods --all-namespaces
```

### 9. **GitHub Actions CI/CD Pipeline**

```yaml
# .github/workflows/deploy.yml
name: Deploy to Kubernetes

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

env:
  AWS_REGION: eu-west-3
  EKS_CLUSTER: landreg-afis-cluster

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3

    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v2
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ${{ env.AWS_REGION }}

    - name: Login to Amazon ECR
      id: login-ecr
      uses: aws-actions/amazon-ecr-login@v1

    - name: Build, tag, and push image to Amazon ECR
      env:
        ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
        ECR_REPOSITORY: landreg-api
        IMAGE_TAG: ${{ github.sha }}
      run: |
        docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG ./backend-api
        docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG

    - name: Update kube config
      run: |
        aws eks update-kubeconfig --region ${{ env.AWS_REGION }} --name ${{ env.EKS_CLUSTER }}

    - name: Deploy to EKS
      run: |
        chmod +x ./scripts/deploy.sh
        ./scripts/deploy.sh
```

### 10. **Storage Class Configuration**

```yaml
# storage-class.yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: fast-ssd
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  fsType: ext4
  encrypted: "true"
reclaimPolicy: Retain
allowVolumeExpansion: true
volumeBindingMode: WaitForFirstConsumer
```

This IaC configuration provides:

1. **Infrastructure Provisioning**: Terraform scripts to create the EKS cluster with node groups matching your server specifications
2. **Service Deployment**: Kubernetes manifests for all services (PostgreSQL, MongoDB, Kafka, Redis, API, AFIS components)
3. **Auto-scaling**: HPA configuration for AFIS workers based on load
4. **Monitoring**: Complete observability stack with Prometheus, Grafana, and ELK
5. **CI/CD**: GitHub Actions pipeline for automated deployment
6. **Storage**: Persistent storage configuration for stateful applications
7. **Networking**: Service discovery and load balancing configurations

The setup follows your architecture with:
- Database nodes (t3.large) for PostgreSQL/MongoDB
- Middleware nodes (t3.xlarge) for Kafka/Redis
- API nodes (t3.large) for backend API
- AFIS nodes (t3.xlarge) for master and workers
- Monitoring nodes (t3.xlarge) for observability stack

The AFIS workers are automatically scaled based on your rule of 1 worker per 20,000 fingerprints, with HPA for dynamic scaling based on actual load.