output "cluster_name" {
  description = "Nome do cluster Kind criado"
  value       = var.cluster_name
}

output "cluster_context" {
  description = "Contexto kubectl para acessar o cluster"
  value       = "kind-${var.cluster_name}"
}

output "app_port_forward" {
  description = "Comando para acessar a aplicacao localmente"
  value       = "kubectl port-forward service/auto-center-fiap-service 8080:80 -n auto-center"
}

output "swagger_url" {
  description = "URL do Swagger UI (execute o port-forward acima primeiro)"
  value       = "http://localhost:8080/swagger-ui/index.html"
}

output "destroy_command" {
  description = "Comando para remover toda a infraestrutura (cluster incluido)"
  value       = "terraform destroy"
}
