terraform {
  required_version = ">= 1.6.0"

  required_providers {
    # Provider para gerenciar recursos Kubernetes
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.27"
    }
    # Provider para instalar charts Helm (Metrics Server para o HPA)
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.12"
    }
    # Provider para executar comandos locais (criar cluster Kind, build Docker)
    null = {
      source  = "hashicorp/null"
      version = "~> 3.0"
    }
  }
}
