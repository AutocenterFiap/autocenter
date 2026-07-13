# ──────────────────────────────────────────────────────────────────────────────
# CLUSTER KIND — criado automaticamente pelo Terraform
# Nenhum comando manual e necessario. O cluster e criado aqui.
# ──────────────────────────────────────────────────────────────────────────────
resource "null_resource" "kind_cluster" {
  triggers = {
    cluster_name = var.cluster_name
  }

  provisioner "local-exec" {
    command = <<-EOT
      set -e
      if kind get clusters 2>/dev/null | grep -q "^${var.cluster_name}$"; then
        echo "[Kind] Cluster '${var.cluster_name}' ja existe, pulando criacao."
      else
        echo "[Kind] Criando cluster '${var.cluster_name}'..."
        cat <<KINDCONFIG | kind create cluster --name "${var.cluster_name}" --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    kubeadmConfigPatches:
      - |
        kind: InitConfiguration
        nodeRegistration:
          kubeletExtraArgs:
            node-labels: "ingress-ready=true"
  - role: worker
  - role: worker
KINDCONFIG
      fi
      echo "[Kind] Exportando kubeconfig..."
      kind export kubeconfig --name "${var.cluster_name}"
      kubectl config use-context "kind-${var.cluster_name}"
      echo "[Kind] Aguardando nodes ficarem prontos..."
      kubectl wait --for=condition=Ready nodes --all --timeout=180s
      echo "[Kind] Cluster pronto!"
    EOT
  }

  provisioner "local-exec" {
    when    = destroy
    command = "kind delete cluster --name ${self.triggers.cluster_name} 2>/dev/null || true"
  }
}

# ──────────────────────────────────────────────────────────────────────────────
# BUILD DA IMAGEM DOCKER + CARGA NO CLUSTER KIND
# Realiza o build da aplicacao e carrega a imagem no cluster local.
# Dispara novamente se o Dockerfile ou o pom.xml mudarem.
# ──────────────────────────────────────────────────────────────────────────────
resource "null_resource" "docker_build_and_load" {
  triggers = {
    cluster_ready   = null_resource.kind_cluster.id
    dockerfile_hash = filemd5("${path.module}/../docker/Dockerfile")
    pom_hash        = filemd5("${path.module}/../pom.xml")
  }

  provisioner "local-exec" {
    working_dir = "${path.module}/.."
    command     = <<-EOT
      set -e
      echo "[Docker] Fazendo build da imagem ${var.app_image}..."
      docker build -t ${var.app_image} -f docker/Dockerfile .
      echo "[Kind] Carregando imagem '${var.app_image}' no cluster '${var.cluster_name}'..."
      kind load docker-image ${var.app_image} --name ${var.cluster_name}
      echo "[OK] Imagem disponivel no cluster!"
    EOT
  }

  depends_on = [null_resource.kind_cluster]
}

# ──────────────────────────────────────────────────────────────────────────────
# PROVIDERS: Kubernetes e Helm apontam para o cluster Kind criado acima
# ──────────────────────────────────────────────────────────────────────────────
locals {
  kubeconfig_path = pathexpand("~/.kube/config")
  kind_context    = "kind-${var.cluster_name}"
}

provider "kubernetes" {
  config_path    = local.kubeconfig_path
  config_context = local.kind_context
}

provider "helm" {
  kubernetes {
    config_path    = local.kubeconfig_path
    config_context = local.kind_context
  }
}

# ──────────────────────────────────────────────────────────────────────────────
# METRICS SERVER
# Necessario para o HPA funcionar (coleta metricas de CPU/memoria).
# ──────────────────────────────────────────────────────────────────────────────
resource "helm_release" "metrics_server" {
  name       = "metrics-server"
  repository = "https://kubernetes-sigs.github.io/metrics-server/"
  chart      = "metrics-server"
  namespace  = "kube-system"

  # Kind usa TLS auto-assinado nos nos — kubelet-insecure-tls e necessario
  set {
    name  = "args[0]"
    value = "--kubelet-insecure-tls"
  }

  depends_on = [null_resource.docker_build_and_load]
}
