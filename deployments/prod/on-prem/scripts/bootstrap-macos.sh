#!/bin/bash
#
# LandReg - Bootstrap Script for macOS
# Configure un Mac avec Docker, Kubernetes, Helm, Terraform, AWS CLI
#
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}==============================================${NC}"
echo -e "${BLUE}  LandReg - macOS Bootstrap Script${NC}"
echo -e "${BLUE}==============================================${NC}"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="$SCRIPT_DIR/bootstrap-macos-$(date +%Y%m%d-%H%M%S).log"

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

check_homebrew() {
    check_command brew
}

install_homebrew() {
    log "Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    success "Homebrew installed"
}

install_docker() {
    log "Installing Docker Desktop..."
    
    if check_command docker; then
        success "Docker already installed: $(docker --version)"
        return 0
    fi

    if check_homebrew; then
        brew install --cask docker
    else
        error "Docker Desktop requires Homebrew. Please install from https://docs.docker.com/desktop/install/mac-install/"
    fi
    
    success "Docker Desktop installed"
}

install_kubectl() {
    log "Installing kubectl..."
    
    if check_command kubectl; then
        success "kubectl already installed: $(kubectl version --client --short 2>/dev/null || kubectl version --client)"
        return 0
    fi

    KUBECTL_VERSION="1.28.0"
    
    curl -LO "https://dl.k8s.io/release/v${KUBECTL_VERSION}/bin/darwin/amd64/kubectl"
    chmod +x kubectl
    sudo mv kubectl /usr/local/bin/kubectl
    rm kubectl
    
    success "kubectl installed: $(kubectl version --client --short 2>/dev/null || kubectl version --client)"
}

install_helm() {
    log "Installing Helm..."
    
    if check_command helm; then
        success "Helm already installed: $(helm version --short)"
        return 0
    fi

    brew install helm
    
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

    brew install terraform
    
    success "Terraform installed: $(terraform version | head -1)"
}

install_aws_cli() {
    log "Installing AWS CLI v2..."
    
    if check_command aws && aws --version | grep -q "aws-cli/2"; then
        success "AWS CLI v2 already installed: $(aws --version)"
        return 0
    fi
    
    curl -fsSL https://awscli.amazonaws.com/AWSCLIV2.pkg -o AWSCLIV2.pkg
    sudo installer -pkg AWSCLIV2.pkg -target /
    rm AWSCLIV2.pkg
    
    success "AWS CLI installed: $(aws --version)"
}

install_eksctl() {
    log "Installing eksctl..."
    
    if check_command eksctl; then
        success "eksctl already installed: $(eksctl version)"
        return 0
    fi

    brew install eksctl
    
    success "eksctl installed: $(eksctl version)"
}

install_utility_tools() {
    log "Installing utility tools..."
    
    if check_homebrew; then
        brew install \
            wget \
            curl \
            git \
            jq \
            tree \
            htop \
            watch
    else
        info "Homebrew not found, skipping some tools"
    fi
    
    success "Utility tools installed"
}

configure_shell() {
    log "Configuring shell environment..."
    
    if [ -f "$HOME/.zshrc" ]; then
        SHELL_RC="$HOME/.zshrc"
    else
        SHELL_RC="$HOME/.bash_profile"
    fi
    
    cat >> "$SHELL_RC" << 'EOF'

# LandReg aliases
alias ll='ls -la'
alias k='kubectl'
alias kgp='kubectl get pods'
alias kgs='kubectl get services'
alias kga='kubectl get all'
alias klf='kubectl logs -f'
alias h='helm'
alias hg='helm get all'
alias hls='helm list'

# Kubernetes autocomplete
source <(kubectl completion zsh 2>/dev/null || kubectl completion bash)
source <(helm completion zsh 2>/dev/null || helm completion bash)
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
        echo -e "  Docker:           $(docker --version 2>/dev/null || echo 'Docker Desktop required')"
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

main() {
    log "Starting macOS bootstrap installation..."
    log "Log file: $LOG_FILE"
    echo ""
    
    if ! check_homebrew; then
        info "Homebrew not found, installing..."
        install_homebrew
    else
        success "Homebrew already installed"
    fi
    
    install_utility_tools
    install_docker
    install_aws_cli
    install_kubectl
    install_helm
    install_terraform
    install_eksctl
    configure_shell
    
    echo ""
    verify_installation
    
    echo ""
    echo -e "${BLUE}==============================================${NC}"
    echo -e "${BLUE}  Bootstrap Complete!${NC}"
    echo -e "${BLUE}==============================================${NC}"
    echo ""
    echo "Next steps:"
    echo "  1. Start Docker Desktop"
    echo "  2. Configure AWS: aws configure"
    echo "  3. Deploy infrastructure: cd deployments/prod && make plan"
    echo ""
    echo "Log file saved to: $LOG_FILE"
}

main "$@"
