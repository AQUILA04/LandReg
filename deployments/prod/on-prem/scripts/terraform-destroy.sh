#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
TERRAFORM_DIR="$PROJECT_ROOT/terraform"

echo "=========================================="
echo "  Terraform Destroy"
echo "=========================================="
echo ""
echo "WARNING: This will destroy ALL resources!"
echo "Type 'yes' to continue..."

read -r response
if [ "$response" != "yes" ]; then
    echo "Aborted."
    exit 0
fi

cd "$TERRAFORM_DIR"

echo "Destroying Terraform resources..."
terraform destroy -auto-approve

echo ""
echo "=========================================="
echo "  Terraform Destroy Complete"
echo "=========================================="
