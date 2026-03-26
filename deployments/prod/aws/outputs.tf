output "cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = module.eks.cluster_endpoint
}

output "cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "cluster_arn" {
  description = "EKS cluster ARN"
  value       = module.eks.cluster_arn
}

output "vpc_id" {
  description = "VPC ID"
  value       = module.vpc.vpc_id
}

output "cluster_version" {
  description = "Kubernetes cluster version"
  value       = module.eks.cluster_version
}

output "node_groups" {
  description = "Node group information"
  value       = module.eks.eks_managed_node_groups
}

output "afis_worker_count" {
  description = "Calculated number of AFIS workers"
  value       = local.afis_worker_count
}

output "region" {
  description = "AWS region"
  value       = var.aws_region
}
