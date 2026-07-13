# ─── Cluster ──────────────────────────────────────────────────────────────────
variable "cluster_name" {
  description = "Nome do cluster Kind"
  type        = string
  default     = "auto-center"
}

# ─── Imagem da aplicação ───────────────────────────────────────────────────────
variable "app_image" {
  description = "Imagem Docker da aplicação (registry/nome:tag)"
  type        = string
  default     = "auto-center-fiap:latest"
}

variable "app_replicas" {
  description = "Número inicial de réplicas da aplicação"
  type        = number
  default     = 2
}

# ─── MySQL ────────────────────────────────────────────────────────────────────
variable "mysql_root_password" {
  description = "Senha root do MySQL"
  type        = string
  sensitive   = true
}

variable "mysql_database" {
  description = "Nome do banco de dados"
  type        = string
  default     = "autocenter"
}

variable "mysql_user" {
  description = "Usuário da aplicação no MySQL"
  type        = string
  default     = "autocenter_user"
}

variable "mysql_password" {
  description = "Senha do usuário da aplicação no MySQL"
  type        = string
  sensitive   = true
}

# ─── Infisical ────────────────────────────────────────────────────────────────
variable "infisical_client_id" {
  description = "Client ID da Machine Identity do Infisical"
  type        = string
  sensitive   = true
}

variable "infisical_client_secret" {
  description = "Client Secret da Machine Identity do Infisical"
  type        = string
  sensitive   = true
}

variable "infisical_project_id" {
  description = "Project ID do Infisical"
  type        = string
  sensitive   = true
}

variable "infisical_environment" {
  description = "Environment do Infisical (prod, staging, dev)"
  type        = string
  default     = "prod"
}

variable "infisical_secret_path" {
  description = "Secret path do Infisical"
  type        = string
  default     = "/"
}

