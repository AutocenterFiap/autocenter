variable "app_image" {
  description = "Imagem Docker da aplicacao no ECR, com tag (ex: 123456789012.dkr.ecr.us-east-1.amazonaws.com/autocenter-fiap:abc123)"
  type        = string
}

variable "app_replicas" {
  description = "Numero inicial de replicas do Deployment (default reduzido para 1 devido a capacidade limitada dos nodes t3.micro do cluster EKS)"
  type        = number
  default     = 1
}

variable "db_name" {
  description = "Nome do banco de dados no RDS (deve ser igual ao db_name do workspace 'database')"
  type        = string
  default     = "autocenter"
}

variable "db_username" {
  description = "Usuario do MySQL no RDS (deve ser igual ao db_username do workspace 'database')"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Senha do MySQL no RDS (deve ser igual ao db_password do workspace 'database')"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Valor de sistema.seguranca.chave.secreta em producao"
  type        = string
  sensitive   = true
}

variable "dd_api_key" {
  description = "API Key da organizacao Datadog (Organization Settings > API Keys)"
  type        = string
  sensitive   = true
}

variable "dd_site" {
  description = "Site do Datadog (ex: datadoghq.com, us5.datadoghq.com, datadoghq.eu)"
  type        = string
  default     = "datadoghq.com"
}
