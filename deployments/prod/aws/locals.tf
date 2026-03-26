locals {
  required_workers  = ceil(var.fingerprint_count / 20000)
  afis_worker_count = max(var.afis_worker_count, local.required_workers)

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}
