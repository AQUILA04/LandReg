#!/bin/bash
#
# LandReg - Bootstrap Script
# Configure un serveur neuf avec Docker, Kubernetes, Helm, Terraform, AWS CLI
#
# Usage:
#   ./bootstrap-server.sh          # Installation complete
#   ./bootstrap-server.sh --docker  # Docker only
#   ./bootstrap-server.sh --k8s    # Kubernetes tools only
#   ./bootstrap-server.sh --help    # Show help
#
set -e

INSTALL_DOCKER=true
INSTALL_K8S=true
INSTALL_ALL=true

show_help() {
    cat << EOF
LandReg Bootstrap Script

Usage: ./bootstrap-server.sh [OPTIONS]

Options:
    --all       Install all components (default)
    --docker    Install only Docker
    --k8s       Install only Kubernetes tools (kubectl, helm, eksctl)
    --aws       Install only AWS CLI
    --help      Show this help message

Examples:
    ./bootstrap-server.sh              # Full installation
    ./bootstrap-server.sh --docker     # Docker only
    ./bootstrap-server.sh --k8s        # Kubernetes tools only

Supported OS:
    - Ubuntu/Debian
    - CentOS/RHEL/Amazon Linux
EOF
    exit 0
}

for arg in "$@"; do
    case $arg in
        --help)
            show_help
            ;;
        --all)
            INSTALL_ALL=true
            ;;
        --docker)
            INSTALL_DOCKER=true
            INSTALL_K8S=false
            INSTALL_ALL=false
            ;;
        --k8s)
            INSTALL_DOCKER=false
            INSTALL_K8S=true
            INSTALL_ALL=false
            ;;
        --aws)
            INSTALL_DOCKER=false
            INSTALL_K8S=false
            INSTALL_ALL=false
            ;;
    esac
done

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}==============================================${NC}"
echo -e "${BLUE}  LandReg - Server Bootstrap Script${NC}"
echo -e "${BLUE}==============================================${NC}"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="$SCRIPT_DIR/bootstrap-$(date +%Y%m%d-%H%M%S).log"

log() {
    echo -e "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[ERROR] $1${NC}" | tee -a "$LOG_FILE"
    exit 1
}

success() {
    echo -e "${GREEN}[OK] $1${NC}" | tee -a "$LOG_FILE"
}

info() {
    echo -e "${YELLOW}[INFO] $1${NC}" | tee -a "$LOG_FILE"
}

check_command() {
    command -v "$1" &>/dev/null
}

detect_os() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$ID
        VERSION=$VERSION_ID
    elif [ -f /etc/centos-release ]; then
        OS="centos"
    elif [ -f /etc/redhat-release ]; then
        OS="rhel"
    else
        OS="unknown"
    fi
    log "Detected OS: $OS $VERSION"
}

install_docker() {
    log "Installing Docker..."
    
    if check_command docker; then
        success "Docker already installed: $(docker --version)"
        return 0
    fi

    case "$OS" in
        ubuntu|debian)
            export DEBIAN_FRONTEND=noninteractive
            
            sudo apt-get update
            sudo apt-get install -y \
                apt-transport-https \
                ca-certificates \
                curl \
                gnupg \
                lsb-release
            
            curl -fsSL https://download.docker.com/linux/${OS}/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
            
            echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/${OS} $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
            
            sudo apt-get update
            sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
            
            sudo usermod -aG docker "$USER"
            ;;
        centos|rhel|amzn)
            sudo yum install -y yum-utils
            sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
            sudo yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
            sudo systemctl start docker
            sudo systemctl enable docker
            sudo usermod -aG docker "$USER"
            ;;
        *)
            error "Unsupported OS: $OS"
            ;;
    esac
    
    sudo systemctl enable docker
    sudo systemctl start docker
    
    success "Docker installed: $(docker --version)"
}

install_kubectl() {
    log "Installing kubectl..."
    
    if check_command kubectl; then
        success "kubectl already installed: $(kubectl version --client --short 2>/dev/null || kubectl version --client)"
        return 0
    fi

    KUBECTL_VERSION="1.28.0"
    
    curl -LO "https://dl.k8s.io/release/v${KUBECTL_VERSION}/bin/linux/amd64/kubectl"
    sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
    rm kubectl
    
    success "kubectl installed: $(kubectl version --client --short 2>/dev/null || kubectl version --client)"
}

install_helm() {
    log "Installing Helm..."
    
    if check_command helm; then
        success "Helm already installed: $(helm version --short)"
        return 0
    fi

    HELM_VERSION="v3.14.0"
    
    curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
    chmod 700 get_helm.sh
    sudo ./get_helm.sh
    rm get_helm.sh
    
    helm repo add bitnami https://charts.bitnami.com/bitnami
    helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
    helm repo add grafana https://grafana.github.io/helm-charts
    helm repo add elastic https://helm.elastic.co
    helm repo update
    
    success "Helm installed: $(helm version --short)"
}

install_terraform() {
    log "Installing Terraform..."
    
    if check_command terraform; then
        success "Terraform already installed: $(terraform version | head -1)"
        return 0
    fi

    TERRAFORM_VERSION="1.6.6"
    
    sudo apt-get install -y gnupg software-properties-common
    
    wget -O- https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
    
    echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
    
    sudo apt-get update
    sudo apt-get install -y terraform
    
    success "Terraform installed: $(terraform version | head -1)"
}

install_aws_cli() {
    log "Installing AWS CLI v2..."
    
    if check_command aws && aws --version | grep -q "aws-cli/2"; then
        success "AWS CLI v2 already installed: $(aws --version)"
        return 0
    fi
    
    curl -fsSL https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o "awscliv2.zip"
    unzip -q awscliv2.zip
    sudo ./aws/install
    rm -rf aws awscliv2.zip
    
    success "AWS CLI installed: $(aws --version)"
}

install_eksctl() {
    log "Installing eksctl..."
    
    if check_command eksctl; then
        success "eksctl already installed: $(eksctl version)"
        return 0
    fi

    ARCH=$(uname -m)
    if [ "$ARCH" = "x86_64" ]; then
        ARCH="amd64"
    fi
    
    curl -fsSL "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_Linux_${ARCH}.tar.gz" | tar -xz -C /tmp
    sudo mv /tmp/eksctl /usr/local/bin
    
    success "eksctl installed: $(eksctl version)"
}

install_utility_tools() {
    log "Installing utility tools..."
    
    case "$OS" in
        ubuntu|debian)
            sudo apt-get install -y \
                curl \
                wget \
                git \
                vim \
                jq \
                unzip \
                zip \
                tar \
                htop \
                tree \
                glances
            ;;
        centos|rhel|amzn)
            sudo yum install -y \
                curl \
                wget \
                git \
                vim \
                jq \
                unzip \
                zip \
                tar \
                htop \
                tree
            ;;
    esac
    
    success "Utility tools installed"
}

configure_docker() {
    log "Configuring Docker..."
    
    sudo mkdir -p /etc/docker
    cat > /tmp/daemon.json << 'EOF'
{
    "log-driver": "json-file",
    "log-opts": {
        "max-size": "10m",
        "max-file": "3"
    },
    "storage-driver": "overlay2",
    "live-restore": true
}
EOF
    sudo mv /tmp/daemon.json /etc/docker/daemon.json
    
    sudo systemctl daemon-reload
    sudo systemctl restart docker
    
    success "Docker configured"
}

configure_shell() {
    log "Configuring shell environment..."
    
    cat >> ~/.bashrc << 'EOF'

# LandReg aliases
alias ll='ls -la'
alias k='kubectl'
alias kgp='kubectl get pods'
alias kgs='kubectl get services'
alias kga='kubectl get all'
alias klf='kubectl logs -f'
alias kdp='kubectl describe pod'
alias kds='kubectl describe service'
alias h='helm'
alias hg='helm get all'
alias hls='helm list'
alias ktx='kubectl ctx'
alias kns='kubectl ns'

# Kubernetes autocomplete
source <(kubectl completion bash)
source <(helm completion bash)

# AWS autocomplete
complete -C '/usr/local/bin/aws_completer' aws
EOF
    
    success "Shell configured"
}

verify_installation() {
    log "Verifying installation..."
    
    echo ""
    echo -e "${BLUE}==============================================${NC}"
    echo -e "${BLUE}  Installation Verification${NC}"
    echo -e "${BLUE}==============================================${NC}"
    echo ""
    
    local all_ok=true
    
    check_command docker && {
        echo -e "  Docker:           $(docker --version)"
    } || { echo -e "  ${RED}Docker: NOT INSTALLED${NC}"; all_ok=false; }
    
    check_command kubectl && {
        echo -e "  kubectl:          $(kubectl version --client --short 2>/dev/null || kubectl version --client)"
    } || { echo -e "  ${RED}kubectl: NOT INSTALLED${NC}"; all_ok=false; }
    
    check_command helm && {
        echo -e "  Helm:             $(helm version --short)"
    } || { echo -e "  ${RED}Helm: NOT INSTALLED${NC}"; all_ok=false; }
    
    check_command terraform && {
        echo -e "  Terraform:        $(terraform version | head -1)"
    } || { echo -e "  ${RED}Terraform: NOT INSTALLED${NC}"; all_ok=false; }
    
    check_command aws && {
        echo -e "  AWS CLI:          $(aws --version)"
    } || { echo -e "  ${RED}AWS CLI: NOT INSTALLED${NC}"; all_ok=false; }
    
    check_command eksctl && {
        echo -e "  eksctl:           $(eksctl version)"
    } || { echo -e "  ${RED}eksctl: NOT INSTALLED${NC}"; all_ok=false; }
    
    echo ""
    
    if [ "$all_ok" = true ]; then
        success "All tools installed successfully!"
        return 0
    else
        error "Some tools failed to install"
    fi
}

install_k8s_infrastructure() {
    log "Installing Kubernetes infrastructure components..."
    
    if ! kubectl cluster-info &>/dev/null; then
        info "Kubernetes cluster not available. Skipping K8s components."
        info "Run this script again after joining the cluster."
        return 0
    fi
    
    echo ""
    info "This will install the following components:"
    echo "  - local-path-provisioner (Storage)"
    echo "  - MetalLB (LoadBalancer)"
    echo "  - Nginx Ingress Controller"
    echo ""
    
    if [ "$AUTO_YES" = true ]; then
        confirm="y"
    else
        echo -n "Continue? [Y/n]: "
        read -r confirm
    fi
    
    if [ "$confirm" != "n" ] && [ "$confirm" != "N" ]; then
        install_local_path_provisioner
        install_metallb
        install_nginx_ingress
    else
        info "Skipped K8s infrastructure components"
    fi
}

install_local_path_provisioner() {
    log "Installing Local Path Provisioner..."
    
    kubectl apply -f https://raw.githubusercontent.com/rancher/local-path-provisioner/master/deploy/local-path-storage.yaml
    
    kubectl patch storageclass standard -p '{"metadata":{"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'
    
    success "Local Path Provisioner installed"
}

install_metallb() {
    log "Installing MetalLB..."
    
    METALLB_VERSION="v0.13.12"
    
    kubectl apply -f "https://raw.githubusercontent.com/metallb/metallb/${METALLB_VERSION}/config/manifests/metallb-native.yaml"
    
    cat << 'METALLB_EOF' | kubectl apply -f -
apiVersion: v1
kind: Namespace
metadata:
  name: metallb-system
---
apiVersion: v1
kind: ConfigMap
metadata:
  namespace: metallb-system
  name: metallb-config
data:
  config: |
    address-pools:
      primary:
        protocol: layer2
        addresses:
        - 192.168.1.240-192.168.1.250
METALLB_EOF
    
    info "MetalLB installed. Update metallb-config if needed with your IP range."
    success "MetalLB installed"
}

install_nginx_ingress() {
    log "Installing Nginx Ingress Controller..."
    
    helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
    helm repo update ingress-nginx
    
    helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
        --namespace ingress-nginx \
        --create-namespace \
        --set controller.service.type=LoadBalancer \
        --set controller.publishService.enabled=true \
        --wait --timeout 5m
    
    success "Nginx Ingress Controller installed"
}

main() {
    detect_os
    
    log "Starting bootstrap installation..."
    log "Log file: $LOG_FILE"
    echo ""
    
    install_utility_tools
    
    if [ "$INSTALL_ALL" = true ] || [ "$INSTALL_DOCKER" = true ]; then
        install_docker
        configure_docker
    fi
    
    if [ "$INSTALL_ALL" = true ] || [ "$INSTALL_K8S" = true ]; then
        install_aws_cli
        install_kubectl
        install_helm
        install_terraform
        install_eksctl
    fi
    
    configure_shell
    
    echo ""
    verify_installation
    
    echo ""
    echo -e "${BLUE}==============================================${NC}"
    echo -e "${BLUE}  Bootstrap Complete!${NC}"
    echo -e "${BLUE}==============================================${NC}"
    echo ""
    
    if [ "$INSTALL_ALL" = true ] || [ "$INSTALL_DOCKER" = true ]; then
        echo "  1. Re-login or run: newgrp docker"
    fi
    
    echo "  2. Configure AWS: aws configure (optional for on-prem)"
    echo "  3. Setup Kubernetes cluster (kubeadm init on master)"
    echo "  4. Join worker nodes (kubeadm join)"
    echo "  5. Install K8s infrastructure: sudo ./bootstrap-server.sh --k8s-infra"
    echo "  6. Deploy LandReg: cd on-prem && ./scripts/deploy.sh"
    echo ""
    echo "Log file saved to: $LOG_FILE"
}

main "$@"
