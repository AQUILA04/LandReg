module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.0.0"

  name = "${var.project_name}-vpc"
  cidr = var.vpc_cidr

  azs             = var.availability_zones
  private_subnets = var.private_subnets
  public_subnets  = var.public_subnets

  enable_nat_gateway     = true
  enable_vpn_gateway     = false
  enable_dns_hostnames   = true
  enable_dns_support     = true
  one_nat_gateway_per_az = true

  tags = local.tags
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "19.0.0"

  cluster_name    = "${var.project_name}-cluster"
  cluster_version = var.cluster_version

  vpc_id                         = module.vpc.vpc_id
  subnet_ids                     = module.vpc.private_subnets
  cluster_endpoint_public_access = true

  eks_managed_node_groups = {
    database = {
      desired_size = 2
      min_size     = 2
      max_size     = 4

      instance_types = ["t3.large"]
      capacity_type  = "ON_DEMAND"

      labels = {
        "node-type" = "database"
        "tier"      = "data"
      }

      tags = {
        Name = "database-node"
      }
    }

    middleware = {
      desired_size = 2
      min_size     = 2
      max_size     = 3

      instance_types = ["t3.xlarge"]
      capacity_type  = "ON_DEMAND"

      labels = {
        "node-type" = "middleware"
        "tier"      = "middleware"
      }

      tags = {
        Name = "middleware-node"
      }
    }

    api = {
      desired_size = 2
      min_size     = 2
      max_size     = 5

      instance_types = ["t3.large"]
      capacity_type  = "ON_DEMAND"

      labels = {
        "node-type" = "api"
        "tier"      = "application"
      }

      tags = {
        Name = "api-node"
      }
    }

    afis-master = {
      desired_size = 2
      min_size     = 2
      max_size     = 3

      instance_types = ["t3.xlarge"]
      capacity_type  = "ON_DEMAND"

      labels = {
        "node-type" = "afis-master"
        "tier"      = "processing"
      }

      tags = {
        Name = "afis-master-node"
      }
    }

    afis-workers = {
      desired_size = local.afis_worker_count
      min_size     = 1
      max_size     = 10

      instance_types = ["t3.xlarge"]
      capacity_type  = "ON_DEMAND"

      labels = {
        "node-type" = "afis-worker"
        "tier"      = "processing"
      }

      tags = {
        Name = "afis-worker-node"
      }
    }

    monitoring = {
      desired_size = 1
      min_size     = 1
      max_size     = 2

      instance_types = ["t3.xlarge"]
      capacity_type  = "ON_DEMAND"

      labels = {
        "node-type" = "monitoring"
        "tier"      = "observability"
      }

      tags = {
        Name = "monitoring-node"
      }
    }
  }

  tags = local.tags
}

data "aws_eks_cluster" "cluster" {
  name = module.eks.cluster_name
}

data "aws_eks_cluster_auth" "cluster" {
  name = module.eks.cluster_name
}

provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
  token                  = data.aws_eks_cluster_auth.cluster.token
}

provider "helm" {
  alias = "eks"
}
