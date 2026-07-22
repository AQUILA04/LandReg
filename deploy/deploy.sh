#!/usr/bin/env bash
set -euo pipefail

PROJECT_NAME="landreg"
GITHUB_RAW="https://raw.githubusercontent.com/aquila04/landreg/main/deploy"

if [[ "$1" == "--force-update" || "$1" == "-fu" ]]; then
    curl -sSL "$GITHUB_RAW/update-deploy.sh" | bash
    shift
    exec "/opt/$PROJECT_NAME/deploy/deploy.sh" "$@"
fi

ENV="$1"
FRONTEND_IMAGE="${2:-}"
OPTIMIZE_IMAGE="${3:-}"
AFIS_MASTER_IMAGE="${4:-}"
AFIS_SERVICE_IMAGE="${5:-}"

STACK_DIR="/opt/$PROJECT_NAME/$ENV"
ENV_FILE="$STACK_DIR/.env"
RELEASES_DIR="$STACK_DIR/releases"

set_env_var() {
  local key="$1" val="$2" file="$ENV_FILE"
  if grep -q -E "^${key}=" "$file" 2>/dev/null; then
    tmp=$(mktemp)
    sed "s~^${key}=.*~${key}=${val}~" "$file" > "$tmp"
    cat "$tmp" > "$file"; rm -f "$tmp"
  else
    echo "${key}=${val}" >> "$file"
  fi
}

# Update images in .env
if [[ -n "$FRONTEND_IMAGE" ]]; then set_env_var "MANAGER_PORTAL_IMAGE" "$FRONTEND_IMAGE"; fi
if [[ -n "$OPTIMIZE_IMAGE" ]]; then set_env_var "OPTIMIZE_LAND_REG_IMAGE" "$OPTIMIZE_IMAGE"; fi
if [[ -n "$AFIS_MASTER_IMAGE" ]]; then set_env_var "AFIS_MASTER_IMAGE" "$AFIS_MASTER_IMAGE"; fi
if [[ -n "$AFIS_SERVICE_IMAGE" ]]; then set_env_var "AFIS_SERVICE_IMAGE" "$AFIS_SERVICE_IMAGE"; fi

# Login to GHCR if credentials are provided
if [[ -n "${GHCR_USERNAME:-}" && -n "${GHCR_TOKEN:-}" ]]; then
    echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin
fi

# Deploy
cd "$STACK_DIR"
cp "/opt/$PROJECT_NAME/deploy/docker-compose.$ENV.yml" docker-compose.yml

echo "Pulling latest images..."
docker compose pull

echo "Starting services..."
docker compose up -d

# Save release metadata
TIMESTAMP=$(date -u +"%Y%m%dT%H%M%SZ")
RELEASE_FILE="$RELEASES_DIR/${ENV}_${TIMESTAMP}.txt"

cat > "$RELEASE_FILE" <<EOF
TIMESTAMP=$TIMESTAMP
FRONTEND_IMAGE=$FRONTEND_IMAGE
OPTIMIZE_LAND_REG_IMAGE=$OPTIMIZE_IMAGE
AFIS_MASTER_IMAGE=$AFIS_MASTER_IMAGE
AFIS_SERVICE_IMAGE=$AFIS_SERVICE_IMAGE
EOF

ln -sf "$RELEASE_FILE" "$RELEASES_DIR/${ENV}_current.txt"

echo "Deployment $ENV completed successfully at $TIMESTAMP"
