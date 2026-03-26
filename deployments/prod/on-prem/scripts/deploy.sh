#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ONPREM_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
K8S_DIR="$ONPREM_DIR/k8s"
HELM_DIR="$ONPREM_DIR/helm-values"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}==========================================${NC}"
echo -e "${BLUE}  LandReg & AFIS System Deployment${NC}"
echo -e "${BLUE}==========================================${NC}"
echo ""

check_prerequisites() {
    echo "Checking prerequisites..."
    
    if ! command -v kubectl &> /dev/null; then
        echo -e "${RED}ERROR: kubectl not found. Please install kubectl first.${NC}"
        exit 1
    fi
    
    if ! command -v helm &> /dev/null; then
        echo -e "${RED}ERROR: helm not found. Please install helm first.${NC}"
        exit 1
    fi
    
    if ! kubectl cluster-info &> /dev/null; then
        echo -e "${RED}ERROR: Cannot connect to Kubernetes cluster. Please check your kubeconfig.${NC}"
        exit 1
    fi
    
    echo -e "  ${GREEN}[OK]${NC} kubectl installed"
    echo -e "  ${GREEN}[OK]${NC} helm installed"
    echo -e "  ${GREEN}[OK]${NC} Kubernetes cluster connected"
    echo ""
}

install_k8s_infrastructure() {
    echo -e "${YELLOW}Step 0: Installing Kubernetes infrastructure (if needed)...${NC}"
    
    if kubectl get ns ingress-nginx &>/dev/null; then
        echo -e "  ${GREEN}[OK]${NC} Nginx Ingress already installed"
    else
        echo "  Installing Nginx Ingress Controller..."
        helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx 2>/dev/null || true
        helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
            --namespace ingress-nginx \
            --create-namespace \
            --set controller.service.type=LoadBalancer \
            --wait --timeout 5m
        echo -e "  ${GREEN}[OK]${NC} Nginx Ingress installed"
    fi
    
    if kubectl get ns metallb-system &>/dev/null; then
        echo -e "  ${GREEN}[OK]${NC} MetalLB already installed"
    else
        echo "  Installing MetalLB..."
        kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.13.12/config/manifests/metallb-native.yaml
        kubectl wait --namespace metallb-system \
          --for=condition=ready pod \
          --selector=app=metallb \
          --timeout=120s 2>/dev/null || true
        echo -e "  ${GREEN}[OK]${NC} MetalLB installed"
    fi
    
    if kubectl get storageclass standard &>/dev/null; then
        echo -e "  ${GREEN}[OK]${NC} Storage class already configured"
    else
        echo "  Installing Local Path Provisioner..."
        kubectl apply -f https://raw.githubusercontent.com/rancher/local-path-provisioner/master/deploy/local-path-storage.yaml
        kubectl patch storageclass standard -p '{"metadata":{"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}' 2>/dev/null || true
        echo -e "  ${GREEN}[OK]${NC} Local Path Provisioner installed"
    fi
    
    echo ""
}

FINGERPRINT_COUNT=${FINGERPRINT_COUNT:-60000}
WORKER_COUNT=$(( (FINGERPRINT_COUNT + 19999) / 20000 ))
WORKER_COUNT=$(( WORKER_COUNT < 1 ? 1 : WORKER_COUNT ))

export POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-$(openssl rand -base64 32)}
export MONGODB_PASSWORD=${MONGODB_PASSWORD:-$(openssl rand -base64 32)}

echo -e "Configuration:"
echo -e "  - Fingerprint Count: ${YELLOW}$FINGERPRINT_COUNT${NC}"
echo -e "  - AFIS Worker Count: ${YELLOW}$WORKER_COUNT${NC}"
echo -e "  - PostgreSQL Password: ${GREEN}[SET]${NC}"
echo -e "  - MongoDB Password: ${GREEN}[SET]${NC}"
echo ""

check_prerequisites
install_k8s_infrastructure

echo -e "${YELLOW}Step 1/10: Adding Helm repositories...${NC}"
helm repo add bitnami https://charts.bitnami.com/bitnami 2>/dev/null || true
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts 2>/dev/null || true
helm repo add grafana https://grafana.github.io/helm-charts 2>/dev/null || true
helm repo add elastic https://helm.elastic.co 2>/dev/null || true
helm repo update

echo ""
echo -e "${YELLOW}Step 2/10: Creating namespaces...${NC}"
kubectl apply -f "$K8S_DIR/namespaces.yaml"

echo ""
echo -e "${YELLOW}Step 3/10: Creating node configuration...${NC}"
if [ -f "$K8S_DIR/nodes-config.yaml" ]; then
    echo -e "  Note: Update ${YELLOW}nodes-config.yaml${NC} with your node names before deploying!"
    echo "  Skipping nodes-config.yaml (manual configuration required)"
else
    echo "  nodes-config.yaml not found, skipping..."
fi

echo ""
echo -e "${YELLOW}Step 4/10: Creating storage classes...${NC}"
kubectl apply -f "$K8S_DIR/storage-class.yaml"

echo ""
echo -e "${YELLOW}Step 5/10: Creating ConfigMaps and Secrets...${NC}"
sed -e "s/\${POSTGRES_PASSWORD}/$(echo "$POSTGRES_PASSWORD" | base64)/g" \
    -e "s/\${MONGODB_PASSWORD}/$(echo "$MONGODB_PASSWORD" | base64)/g" \
    "$K8S_DIR/configmaps.yaml" | kubectl apply -f -
    -e "s/\${MONGODB_PASSWORD}/$(echo "$MONGODB_PASSWORD" | base64)/g" \
    "$K8S_DIR/configmaps.yaml" | kubectl apply -f -

echo ""
echo -e "${YELLOW}Step 6/10: Deploying PostgreSQL...${NC}"
helm upgrade --install postgres bitnami/postgresql \
    --namespace database \
    --values "$HELM_DIR/postgresql-values.yaml" \
    --set auth.existingSecret=db-secrets \
    --wait --timeout 10m

echo ""
echo -e "${YELLOW}Step 7/10: Deploying MongoDB...${NC}"
helm upgrade --install mongodb bitnami/mongodb \
    --namespace database \
    --values "$HELM_DIR/mongodb-values.yaml" \
    --set auth.existingSecret=db-secrets \
    --wait --timeout 10m

echo ""
echo -e "${YELLOW}Step 8/10: Deploying Kafka...${NC}"
helm upgrade --install kafka bitnami/kafka \
    --namespace middleware \
    --values "$HELM_DIR/kafka-values.yaml" \
    --wait --timeout 15m

echo ""
echo -e "${YELLOW}Step 9/10: Deploying Redis...${NC}"
helm upgrade --install redis bitnami/redis \
    --namespace middleware \
    --values "$HELM_DIR/redis-values.yaml" \
    --wait --timeout 10m

echo ""
echo -e "${YELLOW}Step 10/10: Deploying monitoring stack...${NC}"
helm upgrade --install prometheus prometheus-community/prometheus \
    --namespace monitoring \
    --values "$HELM_DIR/prometheus-values.yaml" \
    --wait --timeout 10m

helm upgrade --install grafana grafana/grafana \
    --namespace monitoring \
    --values "$HELM_DIR/grafana-values.yaml" \
    --set adminPassword="$ADMIN_PASSWORD" \
    --wait --timeout 10m

helm upgrade --install elasticsearch elastic/elasticsearch \
    --namespace monitoring \
    --values "$HELM_DIR/elasticsearch-values.yaml" \
    --wait --timeout 10m

helm upgrade --install kibana elastic/kibana \
    --namespace monitoring \
    --values "$HELM_DIR/kibana-values.yaml" \
    --wait --timeout 10m

echo ""
echo "=========================================="
echo "  Application Deployment"
echo "=========================================="
echo ""

echo "Deploying Backend API..."
kubectl apply -f "$K8S_DIR/backend-api-deployment.yaml"

echo "Deploying AFIS Master..."
kubectl apply -f "$K8S_DIR/afis-master-deployment.yaml"

echo "Deploying AFIS Workers (replicas: $WORKER_COUNT)..."
sed "s/AFIS_WORKER_REPLICAS/$WORKER_COUNT/g" "$K8S_DIR/afis-worker-deployment.yaml" | kubectl apply -f -

echo ""
echo "=========================================="
echo "  Deployment Status"
echo "=========================================="
echo ""

echo "Pods:"
kubectl get pods --all-namespaces -o wide

echo ""
echo "Services:"
kubectl get services --all-namespaces

echo ""
echo "Deployments:"
kubectl get deployments --all-namespaces

echo ""
echo "=========================================="
echo "  Deployment Complete!"
echo "=========================================="
echo ""
echo "AFIS Worker count: $WORKER_COUNT (based on $FINGERPRINT_COUNT fingerprints)"
echo ""
echo "Next steps:"
echo "  - Get Grafana password: kubectl get secret --namespace monitoring grafana -o jsonpath=\"{.data.admin-password}\" | base64 --decode"
echo "  - Access services via kubectl port-forward or ingress"
echo "  - Monitor pods: kubectl get pods -A -w"
