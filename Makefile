# =============================================================================
# Makefile — Auto Center FIAP
# =============================================================================
TERRAFORM_DIR := terraform

.PHONY: deploy destroy plan init help

help: ## Mostra esta ajuda
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}'

init: ## Inicializa o Terraform
	cd $(TERRAFORM_DIR) && terraform init -upgrade

plan: ## Exibe o plano de execução (sem aplicar)
	cd $(TERRAFORM_DIR) && terraform plan

deploy: ## Cria o cluster Kind e faz o deploy completo (2 fases)
	@echo "==> Fase 1: Criando cluster Kind..."
	cd $(TERRAFORM_DIR) && terraform init -upgrade && \
	  terraform apply -target=null_resource.kind_cluster -auto-approve
	@echo "==> Fase 2: Deploy completo..."
	cd $(TERRAFORM_DIR) && terraform apply -auto-approve
	@echo "==> Deploy concluido com sucesso!"

destroy: ## Destroi todos os recursos (cluster Kind incluso)
	cd $(TERRAFORM_DIR) && terraform destroy -auto-approve

