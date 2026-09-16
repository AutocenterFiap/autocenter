# ─────────────────────────────────────────────────────────────────────────
# Datadog Agent (DaemonSet) no cluster EKS
#
# Instala o Agent oficial via Helm chart "datadog/datadog" para coletar
# metricas (openmetrics/Prometheus), logs e APM dos pods da aplicacao,
# usando as anotacoes de autodiscovery definidas em kubernetes.tf.
#
# Recursos reduzidos porque o node group do EKS usa instancias pequenas
# (t3.micro/t3.small) com capacidade limitada — ver infraestrutura/eks.node.tf.
# ─────────────────────────────────────────────────────────────────────────

resource "kubernetes_secret" "datadog" {
  metadata {
    name      = "datadog-secret"
    namespace = kubernetes_namespace.autocenter.metadata[0].name
  }

  data = {
    api-key = var.dd_api_key
  }

  type = "Opaque"
}

resource "helm_release" "datadog" {
  name       = "datadog"
  repository = "https://helm.datadoghq.com"
  chart      = "datadog"
  version    = "3.99.0"
  namespace  = kubernetes_namespace.autocenter.metadata[0].name

  values = [
    yamlencode({
      datadog = {
        site = var.dd_site

        apiKeyExistingSecret = kubernetes_secret.datadog.metadata[0].name

        # Nome do cluster para agrupar hosts/checks no Datadog
        clusterName = "eks-autocenter-fiap"

        logs = {
          enabled             = true
          containerCollectAll = true
        }

        # APM (traces) desabilitado: o escopo e metricas (openmetrics) + logs,
        # e a aplicacao nao possui instrumentacao de tracing. Alem disso, com
        # o Cluster Agent desabilitado (para economizar recursos nos nodes
        # t3.micro), o container trace-agent falha ao escrever o token de
        # autenticacao compartilhado em /etc/datadog-agent/auth/token
        # ("read-only file system"), entao mantemos APM desligado.
        apm = {
          portEnabled = false
        }

        # Habilita a coleta via anotacoes ad.datadoghq.com/* (autodiscovery)
        kubelet = {
          tlsVerify = false
        }
      }

      # No EKS, o endpoint de metadados EC2 (IMDS) nao e alcancavel de dentro
      # dos pods, entao o Agent nao consegue detectar o hostname via IMDS e
      # falha com "unable to reliably determine the host name". Esta flag faz
      # o Agent ler o hostname a partir do arquivo local do host
      # (/var/lib/cloud/data/instance-id) em vez de consultar o IMDS.
      providers = {
        eks = {
          ec2 = {
            useHostnameFromFile = true
          }
        }
      }

      # Process Agent e Orchestrator Explorer desabilitados para reduzir
      # consumo de CPU/memoria nos nodes t3.micro do cluster.
      agents = {
        image = {
          tag = "7.83.1"
        }
        containers = {
          agent = {
            resources = {
              requests = {
                cpu    = "100m"
                memory = "128Mi"
              }
              limits = {
                cpu    = "200m"
                memory = "256Mi"
              }
            }
          }
        }
      }

      clusterAgent = {
        enabled = false
      }

      processAgent = {
        enabled                  = false
        processCollectionEnabled = false
      }
    })
  ]

  depends_on = [kubernetes_namespace.autocenter]
}
