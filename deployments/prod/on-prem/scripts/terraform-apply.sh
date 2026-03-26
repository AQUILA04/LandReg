#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DEPLOY_DIR="$PROJECT_ROOT/deployments/prod"
TERRAFORM_DIR="$DEPLOY_DIR/terraform"

echo "=========================================="
echo "  Terraform Apply"
echo "=========================================="
echo ""

cd "$TERRAFORM_DIR"

if [ ! -f "terraform.tfvars" ]; then
    echo "Error: terraform.tfvars not found. Run terraform-plan.sh first."
    exit 1
fi

echo "Applying Terraform changes..."
terraform apply -auto-approve

echo ""
echo "=========================================="
echo "  Terraform Apply Complete"
echo "=========================================="
echo ""

echo "Cluster details:"
terraform output

echo ""
echo "Next steps:"
echo "  1. Update kubeconfig: aws eks update-kubeconfig --region <region> --name <cluster-name>"
echo "  2. Deploy applications: ./scripts/deploy.sh"
