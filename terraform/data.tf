data "terraform_remote_state" "database" {
  backend = "remote"

  config = {
    organization = "autocenter-fiap"
    workspaces = {
      name = "database"
    }
  }
}


data "aws_eks_cluster" "cluster" {
  name = "eks-autocenter-fiap-infraestrutura"
}


data "aws_eks_cluster_auth" "auth" {
  name = "eks-autocenter-fiap-infraestrutura"
}
