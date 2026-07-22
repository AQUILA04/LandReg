#!/usr/bin/env bash
set -euo pipefail

PROJECT_NAME="landreg"

echo "Updating deployment scripts..."
git clone https://github.com/aquila04/landreg.git /tmp/${PROJECT_NAME}_src
cp -r /tmp/${PROJECT_NAME}_src/deploy /opt/${PROJECT_NAME}/deploy.new
rm -rf /tmp/${PROJECT_NAME}_src
chmod +x /opt/${PROJECT_NAME}/deploy.new/*.sh

# Atomic swap
BACKUP="/opt/${PROJECT_NAME}/deploy.old_$(date +%s)"
mv /opt/${PROJECT_NAME}/deploy "$BACKUP"
mv /opt/${PROJECT_NAME}/deploy.new /opt/${PROJECT_NAME}/deploy

echo "Deployment scripts updated successfully."
