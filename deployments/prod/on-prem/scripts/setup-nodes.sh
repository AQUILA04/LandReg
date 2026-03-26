#!/bin/bash
#
# LandReg - Configure Node Labels
# Configure les labels Kubernetes selon l'inventaire infrastructure
#
# Usage:
#   ./setup-nodes.sh              # Mode interactif
#   ./setup-nodes.sh --dry-run    # Afficher les commandes sans exécuter
#

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="$SCRIPT_DIR/../k8s/nodes-config.yaml"

DRY_RUN=false
if [ "$1" = "--dry-run" ]; then
    DRY_RUN=true
fi

echo -e "${BLUE}==============================================${NC}"
echo -e "${BLUE}  Node Labels Configuration${NC}"
echo -e "${BLUE}==============================================${NC}"
echo ""

check_config() {
    if [ ! -f "$CONFIG_FILE" ]; then
        echo -e "${RED}ERROR: $CONFIG_FILE not found${NC}"
        echo "Please create nodes-config.yaml first"
        exit 1
    fi
}

list_current_nodes() {
    echo "Current nodes in cluster:"
    echo ""
    kubectl get nodes --show-labels | head -20
    echo ""
}

label_node() {
    local NODE="$1"
    local LABEL="$2"
    local CMD="kubectl label node $NODE $LABEL --overwrite"
    
    if [ "$DRY_RUN" = true ]; then
        echo -e "${YELLOW}[DRY-RUN]${NC} $CMD"
    else
        echo -e "${BLUE}[LABEL]${NC} $NODE -> $LABEL"
        $CMD
    fi
}

main() {
    check_config
    list_current_nodes
    
    echo -e "${YELLOW}This script will label your nodes for LandReg deployment.${NC}"
    echo ""
    
    if [ "$DRY_RUN" = true ]; then
        echo -e "${YELLOW}DRY-RUN MODE: No changes will be made${NC}"
        echo ""
    fi
    
    echo "Available node labels for LandReg:"
    echo "  - node-type=database       : Pour PostgreSQL, MongoDB"
    echo "  - node-type=middleware     : Pour Kafka, Redis"
    echo "  - node-type=api            : Pour Backend API"
    echo "  - node-type=afis-master    : Pour AFIS Master"
    echo "  - node-type=afis-worker    : Pour AFIS Workers"
    echo "  - node-type=monitoring     : Pour Prometheus, Grafana, ELK"
    echo ""
    
    echo "Enter the node name to label (or 'q' to quit):"
    
    while true; do
        echo ""
        echo -n "Node name: "
        read -r NODE_NAME
        
        if [ "$NODE_NAME" = "q" ] || [ "$NODE_NAME" = "Q" ]; then
            echo "Exiting..."
            exit 0
        fi
        
        if [ -z "$NODE_NAME" ]; then
            continue
        fi
        
        if ! kubectl get node "$NODE_NAME" &>/dev/null; then
            echo -e "${RED}ERROR: Node '$NODE_NAME' not found${NC}"
            continue
        fi
        
        echo ""
        echo "Select label for node '$NODE_NAME':"
        echo "  1) database"
        echo "  2) middleware"
        echo "  3) api"
        echo "  4) afis-master"
        echo "  5) afis-worker"
        echo "  6) monitoring"
        echo "  7) Skip this node"
        echo ""
        echo -n "Choice: "
        read -r CHOICE
        
        case $CHOICE in
            1) label_node "$NODE_NAME" "node-type=database" ;;
            2) label_node "$NODE_NAME" "node-type=middleware" ;;
            3) label_node "$NODE_NAME" "node-type=api" ;;
            4) label_node "$NODE_NAME" "node-type=afis-master" ;;
            5) label_node "$NODE_NAME" "node-type=afis-worker" ;;
            6) label_node "$NODE_NAME" "node-type=monitoring" ;;
            7) echo "Skipped" ;;
            *) echo "Invalid choice" ;;
        esac
    done
    
    echo ""
    echo -e "${GREEN}Node labeling complete!${NC}"
    echo ""
    echo "Verify with:"
    echo "  kubectl get nodes --show-labels | grep node-type"
}

main "$@"
