.PHONY: help aws-plan aws-apply aws-destroy aws-deploy
.PHONY: onprem-deploy onprem-status onprem-logs onprem-bootstrap
.PHONY: all-plan all-apply all-destroy

AWS_DIR := $(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))/aws
ONPREM_DIR := $(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))/on-prem

help:
	@echo "LandReg IaC - Production Makefile"
	@echo "================================="
	@echo ""
	@echo "AWS EKS Deployment:"
	@echo "  make aws-plan       - Run Terraform plan"
	@echo "  make aws-apply      - Apply Terraform changes"
	@echo "  make aws-destroy    - Destroy AWS infrastructure"
	@echo "  make aws-deploy     - Deploy to EKS"
	@echo ""
	@echo "On-Premises Deployment:"
	@echo "  make onprem-bootstrap  - Bootstrap server (install tools)"
	@echo "  make onprem-deploy    - Deploy to local K8s"
	@echo "  make onprem-status    - Show deployment status"
	@echo "  make onprem-logs      - Show logs"
	@echo ""
	@echo "Full Stack:"
	@echo "  make all-plan       - Plan both AWS and on-prem"
	@echo "  make all-apply      - Apply both"

# AWS EKS targets
aws-plan:
	@cd $(AWS_DIR) && terraform init && terraform plan

aws-apply:
	@cd $(AWS_DIR) && terraform apply

aws-destroy:
	@cd $(AWS_DIR) && terraform destroy

aws-deploy:
	@cd $(AWS_DIR) && ../on-prem/scripts/deploy.sh

# On-Premises targets
onprem-bootstrap:
	@chmod +x $(ONPREM_DIR)/scripts/bootstrap-server.sh
	@sudo $(ONPREM_DIR)/scripts/bootstrap-server.sh

onprem-deploy:
	@chmod +x $(ONPREM_DIR)/scripts/deploy.sh
	@$(ONPREM_DIR)/scripts/deploy.sh

onprem-status:
	@echo "=== Pods ===" && kubectl get pods --all-namespaces
	@echo "" && echo "=== Services ===" && kubectl get services --all-namespaces

onprem-logs:
	@kubectl logs -n backend -l app=landreg-api --tail=50
	@kubectl logs -n afis -l app=afis-master --tail=50
	@kubectl logs -n afis -l app=afis-worker --tail=50

# Full stack targets
all-plan:
	@$(MAKE) aws-plan
	@echo "On-prem deployment doesn't require planning"

all-apply:
	@$(MAKE) aws-apply
	@$(MAKE) onprem-deploy
