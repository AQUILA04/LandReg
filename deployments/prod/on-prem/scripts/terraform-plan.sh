#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DEPLOY_DIR="$PROJECT_ROOT/deployments/prod"
TERRAFORM_DIR="$DEPLOY_DIR/terraform"

echo "=========================================="
echo "  Terraform Init & Plan"
echo "=========================================="
echo ""

cd "$TERRAFORM_DIR"

echo "Initializing Terraform..."
terraform init -upgrade

echo ""
echo "Creating tfvars file if not exists..."
if [ ! -f "terraform.tfvars" ]; then
    cp terraform.tfvars.example terraform.tfvars
    echo "Created terraform.tfvars from example"
    echo "Please review and update terraform.tfvars with your values"
fi

echo ""
echo "Running Terraform plan..."
terraform plan -out=tfplan

echo ""
echo "=========================================="
echo "  Terraform Plan Complete"
echo "=========================================="
echo ""
echo "Review the plan above, then run:"
echo "  cd $TERRAFORM_DIR"
echo "  terraform apply tfplan"
