variable "app_image" {
  description = "Imagem Docker da aplicacao no ECR, com tag (ex: 123456789012.dkr.ecr.us-east-1.amazonaws.com/autocenter-fiap:abc1234)"
  type        = string
}

variable "app_replicas" {
  description = "Numero inicial de replicas do Deployment"
  type        = number
  default     = 2
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
