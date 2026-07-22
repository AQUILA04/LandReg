#!/usr/bin/env bash
set -euo pipefail

# Usage: init.sh <env> <frontend_image> <optimize_image> <afis_master_image> <afis_service_image> [args...]

PROJECT_NAME="landreg"
DEPLOY_DIR="/opt/$PROJECT_NAME/deploy"
GITHUB_RAW="https://raw.githubusercontent.com/aquila04/landreg/main/deploy"

if [[ "$1" == "--force-update" || "$1" == "-fu" ]]; then
    bash <(curl -sSL "$GITHUB_RAW/update-deploy.sh")
    shift
    exec "$DEPLOY_DIR/init.sh" "$@"
fi

ENV="$1"
FRONTEND_IMAGE="$2"
OPTIMIZE_IMAGE="$3"
AFIS_MASTER_IMAGE="$4"
AFIS_SERVICE_IMAGE="$5"
shift 5

# Parse remaining arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --ghcr-username) export GHCR_USERNAME="$2"; shift 2 ;;
    --ghcr-token) export GHCR_TOKEN="$2"; shift 2 ;;
    --db-user) export CT_DB_USER="$2"; shift 2 ;;
    --db-password) export CT_DB_PASSWORD="$2"; shift 2 ;;
    --db-name) export CT_DB_NAME="$2"; shift 2 ;;
    --mongo-user) export CT_MONGO_USER="$2"; shift 2 ;;
    --mongo-password) export CT_MONGO_PASSWORD="$2"; shift 2 ;;
    --keycloak-admin-password) export CT_KEYCLOAK_ADMIN_PASSWORD="$2"; shift 2 ;;
    --jwt-secret) export CT_JWT_SECRET="$2"; shift 2 ;;
    --app-hostname-test) export CT_TEST_APP_HOSTNAME="$2"; shift 2 ;;
    --keycloak-hostname-test) export CT_TEST_KEYCLOAK_HOSTNAME="$2"; shift 2 ;;
    --app-hostname-prod) export CT_PROD_APP_HOSTNAME="$2"; shift 2 ;;
    --keycloak-hostname-prod) export CT_PROD_KEYCLOAK_HOSTNAME="$2"; shift 2 ;;
    *) echo "Unknown parameter passed: $1"; exit 1 ;;
  esac
done

# Ensure deploy/ is present
if [[ ! -d "$DEPLOY_DIR" ]]; then
    git clone https://github.com/aquila04/landreg.git /tmp/${PROJECT_NAME}_src
    cp -r /tmp/${PROJECT_NAME}_src/deploy "$DEPLOY_DIR"
    rm -rf /tmp/${PROJECT_NAME}_src
    chmod +x "$DEPLOY_DIR"/*.sh
fi

# First-time setup
SETUP_MARKER="/opt/$PROJECT_NAME/.server_initialized"
if [[ ! -f "$SETUP_MARKER" ]]; then
    bash "$DEPLOY_DIR/setup-server.sh"
    touch "$SETUP_MARKER"
fi

# Always deploy
bash "$DEPLOY_DIR/deploy.sh" "$ENV" "$FRONTEND_IMAGE" "$OPTIMIZE_IMAGE" "$AFIS_MASTER_IMAGE" "$AFIS_SERVICE_IMAGE"
