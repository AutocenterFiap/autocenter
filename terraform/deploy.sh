#!/usr/bin/env bash
# =============================================================================
# deploy.sh — Deploy completo do Auto Center FIAP no Kind
#
# Resolve o problema de chicken-and-egg do Terraform:
#   Os providers "kubernetes" e "helm" precisam que o cluster exista
#   ANTES de serem inicializados. Por isso o deploy é feito em 2 fases:
#
#   Fase 1 → cria o cluster Kind (null_resource.kind_cluster)
#   Fase 2 → aplica todos os demais recursos (namespace, deployments, HPA…)
# =============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

info()    { echo -e "${GREEN}[INFO]${NC} $*"; }
warning() { echo -e "${YELLOW}[WARN]${NC} $*"; }
error()   { echo -e "${RED}[ERRO]${NC} $*" >&2; }

# ─── Pré-requisitos ───────────────────────────────────────────────────────────
for cmd in terraform kind docker kubectl; do
  if ! command -v "$cmd" &>/dev/null; then
    error "Comando '$cmd' não encontrado. Instale-o antes de continuar."
    exit 1
  fi
done

# ─── terraform init (idempotente) ─────────────────────────────────────────────
info "Inicializando Terraform..."
terraform init -upgrade

# ─── Fase 1: Criar o cluster Kind ─────────────────────────────────────────────
info "==> Fase 1: Criando cluster Kind..."
terraform apply \
  -target=null_resource.kind_cluster \
  -auto-approve

info "Cluster Kind criado e kubeconfig exportado."

# ─── Fase 2: Deploy completo ──────────────────────────────────────────────────
info "==> Fase 2: Aplicando todos os recursos..."
terraform apply -auto-approve

info "==> Deploy concluído com sucesso!"

