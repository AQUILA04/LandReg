#!/usr/bin/env bash
set -euo pipefail

PROJECT_NAME="landreg"
ENV="$1"
RELEASES_DIR="/opt/$PROJECT_NAME/$ENV/releases"

# Find the second-to-last release (previous deployment)
PREV_RELEASE=$(ls -t "$RELEASES_DIR/${ENV}_"*.txt | sed -n '2p')
if [[ -z "$PREV_RELEASE" ]]; then
    echo "No previous release found." >&2; exit 1
fi

echo "Rolling back to release: $PREV_RELEASE"

FRONTEND_IMAGE=$(grep '^FRONTEND_IMAGE=' "$PREV_RELEASE" | cut -d= -f2-)
OPTIMIZE_IMAGE=$(grep '^OPTIMIZE_LAND_REG_IMAGE=' "$PREV_RELEASE" | cut -d= -f2-)
AFIS_MASTER_IMAGE=$(grep '^AFIS_MASTER_IMAGE=' "$PREV_RELEASE" | cut -d= -f2-)
AFIS_SERVICE_IMAGE=$(grep '^AFIS_SERVICE_IMAGE=' "$PREV_RELEASE" | cut -d= -f2-)

# Re-deploy with previous images
bash "$(dirname "$0")/deploy.sh" "$ENV" "$FRONTEND_IMAGE" "$OPTIMIZE_IMAGE" "$AFIS_MASTER_IMAGE" "$AFIS_SERVICE_IMAGE"
