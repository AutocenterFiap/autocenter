output "app_service_hostname" {
  description = "Hostname do LoadBalancer criado para a aplicacao"
  value       = try(kubernetes_service.autocenter_app.status[0].load_balancer[0].ingress[0].hostname, null)
}

output "application_service_name" {
  description = "Nome do Service Kubernetes do app"
  value       = kubernetes_service.autocenter_app.metadata[0].name
}