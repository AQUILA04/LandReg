resource "kubernetes_service_account" "terraform" {
  metadata {
    name      = "terraform"
    namespace = "kube-system"
  }
}

resource "kubernetes_cluster_role" "terraform" {
  metadata {
    name = "terraform"
  }

  rule {
    api_groups = [""]
    resources  = ["*"]
    verbs      = ["*"]
  }

  rule {
    api_groups = ["apps"]
    resources  = ["*"]
    verbs      = ["*"]
  }

  rule {
    api_groups = ["autoscaling"]
    resources  = ["*"]
    verbs      = ["*"]
  }

  rule {
    api_groups = ["storage.k8s.io"]
    resources  = ["*"]
    verbs      = ["*"]
  }

  rule {
    api_groups = ["networking.k8s.io"]
    resources  = ["*"]
    verbs      = ["*"]
  }
}

resource "kubernetes_cluster_role_binding" "terraform" {
  metadata {
    name = "terraform"
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = "terraform"
  }

  subject {
    kind      = "ServiceAccount"
    name      = "terraform"
    namespace = "kube-system"
  }
}
