terraform {
  required_version = ">= 1.15.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }

    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "3.2.1"
    }

    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.16"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}
