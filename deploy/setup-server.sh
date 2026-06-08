#!/usr/bin/env bash
set -euo pipefail

PROJECT_NAME="landreg"
BASE_DIR="/opt/$PROJECT_NAME"

echo "Starting server setup for $PROJECT_NAME..."

# 1. Install Docker if not present
if ! command -v docker &> /dev/null; then
    echo "Installing Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sh get-docker.sh
    rm get-docker.sh
fi

# 2. Create directory structure
mkdir -p "$BASE_DIR/test/releases"
mkdir -p "$BASE_DIR/prod/releases"
mkdir -p "$BASE_DIR/traefik"

# 3. Create shared Docker networks
docker network inspect traefik-public >/dev/null 2>&1 || docker network create traefik-public
docker network inspect ${PROJECT_NAME}-test-internal >/dev/null 2>&1 || docker network create ${PROJECT_NAME}-test-internal
docker network inspect ${PROJECT_NAME}-prod-internal >/dev/null 2>&1 || docker network create ${PROJECT_NAME}-prod-internal

# 4. Generate .env files
cat > "$BASE_DIR/test/.env" <<EOF
COMPOSE_PROJECT_NAME=${PROJECT_NAME}-test
DB_USER=${CT_DB_USER:-postgres}
DB_PASSWORD=${CT_DB_PASSWORD:-test_db_pass}
DB_NAME=${CT_DB_NAME:-landreg_test}
MONGO_USER=${CT_MONGO_USER:-root}
MONGO_PASSWORD=${CT_MONGO_PASSWORD:-test_mongo_pass}
KEYCLOAK_ADMIN_PASSWORD=${CT_KEYCLOAK_ADMIN_PASSWORD:-test_kc_pass}
JWT_SECRET=${CT_JWT_SECRET:-test_jwt_secret_12345678901234567890}
APP_HOSTNAME=${CT_TEST_APP_HOSTNAME:-test.landreg.local}
KEYCLOAK_HOSTNAME=${CT_TEST_KEYCLOAK_HOSTNAME:-auth.test.landreg.local}
EOF

cat > "$BASE_DIR/prod/.env" <<EOF
COMPOSE_PROJECT_NAME=${PROJECT_NAME}-prod
DB_USER=${CT_DB_USER:-postgres}
DB_PASSWORD=${CT_DB_PASSWORD:-prod_db_pass}
DB_NAME=${CT_DB_NAME:-landreg_prod}
MONGO_USER=${CT_MONGO_USER:-root}
MONGO_PASSWORD=${CT_MONGO_PASSWORD:-prod_mongo_pass}
KEYCLOAK_ADMIN_PASSWORD=${CT_KEYCLOAK_ADMIN_PASSWORD:-prod_kc_pass}
JWT_SECRET=${CT_JWT_SECRET:-prod_jwt_secret_12345678901234567890}
APP_HOSTNAME=${CT_PROD_APP_HOSTNAME:-landreg.local}
KEYCLOAK_HOSTNAME=${CT_PROD_KEYCLOAK_HOSTNAME:-auth.landreg.local}
EOF

# 5. Setup Traefik
cat > "$BASE_DIR/traefik/docker-compose.yml" <<EOF
version: '3.8'
services:
  traefik:
    image: traefik:v2.10
    container_name: traefik
    command:
      - "--api.insecure=true"
      - "--providers.docker=true"
      - "--providers.docker.exposedbydefault=false"
      - "--entrypoints.web.address=:80"
      - "--entrypoints.web.http.redirections.entryPoint.to=websecure"
      - "--entrypoints.web.http.redirections.entryPoint.scheme=https"
      - "--entrypoints.websecure.address=:443"
      - "--certificatesresolvers.letsencrypt.acme.tlschallenge=true"
      - "--certificatesresolvers.letsencrypt.acme.email=admin@landreg.local"
      - "--certificatesresolvers.letsencrypt.acme.storage=/letsencrypt/acme.json"
    ports:
      - "80:80"
      - "443:443"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock:ro
      - ./letsencrypt:/letsencrypt
    networks:
      - traefik-public
    restart: always

networks:
  traefik-public:
    external: true
EOF

cd "$BASE_DIR/traefik"
docker compose up -d

echo "Server setup completed successfully."
