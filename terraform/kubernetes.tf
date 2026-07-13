# ──────────────────────────────────────────────────────────────────────────────
# NAMESPACE
# ──────────────────────────────────────────────────────────────────────────────
resource "kubernetes_namespace" "auto_center" {
  metadata {
    name = "auto-center"
    labels = {
      app         = "auto-center-fiap"
      environment = "production"
    }
  }

  depends_on = [helm_release.metrics_server]
}

# ──────────────────────────────────────────────────────────────────────────────
# CONFIGMAP — variáveis não-sensíveis
# ──────────────────────────────────────────────────────────────────────────────
resource "kubernetes_config_map" "auto_center" {
  metadata {
    name      = "auto-center-configmap"
    namespace = kubernetes_namespace.auto_center.metadata[0].name
    labels    = { app = "auto-center-fiap" }
  }

  data = {
    SPRING_PROFILES_ACTIVE              = "prod"
    SERVER_PORT                         = "8097"
    SPRING_DATASOURCE_URL               = "jdbc:mysql://mysql-service:3306/${var.mysql_database}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
    SPRING_DATASOURCE_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver"
    SPRING_JPA_DATABASE_PLATFORM        = "org.hibernate.dialect.MySQLDialect"
    SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL = "true"
    SPRING_JPA_HIBERNATE_DDL_AUTO       = "none"
    SPRING_JPA_SHOW_SQL                 = "false"
    SPRING_FLYWAY_ENABLED               = "true"
    SPRING_FLYWAY_LOCATIONS             = "filesystem:/app/db/migration"
    SPRING_FLYWAY_BASELINE_ON_MIGRATE   = "true"
    SISTEMA_TOKEN_EXPIRACAO_MINUTOS     = "30"
    SPRING_CONFIG_ADDITIONAL_LOCATION   = "file:/vault-secrets/"
    INFISICAL_ENVIRONMENT               = var.infisical_environment
    INFISICAL_SECRET_PATH               = var.infisical_secret_path
    LOGGING_LEVEL_ROOT                  = "INFO"
    LOGGING_LEVEL_BR_COM_AUTOCENTERFIAP = "INFO"
  }
}

# ──────────────────────────────────────────────────────────────────────────────
# SECRET — variáveis sensíveis
# ──────────────────────────────────────────────────────────────────────────────
resource "kubernetes_secret" "auto_center" {
  metadata {
    name      = "auto-center-secrets"
    namespace = kubernetes_namespace.auto_center.metadata[0].name
    labels    = { app = "auto-center-fiap" }
  }

  type = "Opaque"

  data = {
    # MySQL (para o container do banco)
    MYSQL_ROOT_PASSWORD = var.mysql_root_password
    MYSQL_DATABASE      = var.mysql_database
    MYSQL_USER          = var.mysql_user
    MYSQL_PASSWORD      = var.mysql_password

    # Infisical (para o init container buscar secrets da aplicação)
    INFISICAL_CLIENT_ID     = var.infisical_client_id
    INFISICAL_CLIENT_SECRET = var.infisical_client_secret
    INFISICAL_PROJECT_ID    = var.infisical_project_id
  }
}

# ──────────────────────────────────────────────────────────────────────────────
# BANCO DE DADOS — MySQL
# ──────────────────────────────────────────────────────────────────────────────
resource "kubernetes_persistent_volume_claim" "mysql" {
  metadata {
    name      = "mysql-pvc"
    namespace = kubernetes_namespace.auto_center.metadata[0].name
    labels    = { app = "mysql", component = "database" }
  }

  spec {
    access_modes = ["ReadWriteOnce"]
    resources {
      requests = { storage = "10Gi" }
    }
  }

  wait_until_bound = false
}

resource "kubernetes_deployment" "mysql" {
  metadata {
    name      = "mysql"
    namespace = kubernetes_namespace.auto_center.metadata[0].name
    labels    = { app = "mysql", component = "database" }
  }

  spec {
    replicas = 1

    selector {
      match_labels = { app = "mysql" }
    }

    strategy {
      type = "Recreate"
    }

    template {
      metadata {
        labels = { app = "mysql", component = "database" }
      }

      spec {
        container {
          name  = "mysql"
          image = "mysql:8.0"

          port {
            container_port = 3306
            name           = "mysql"
          }

          args = [
            "--character-set-server=utf8mb4",
            "--collation-server=utf8mb4_unicode_ci",
            "--default-authentication-plugin=mysql_native_password",
          ]

          env {
            name = "MYSQL_ROOT_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "MYSQL_ROOT_PASSWORD"
              }
            }
          }
          env {
            name = "MYSQL_DATABASE"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "MYSQL_DATABASE"
              }
            }
          }
          env {
            name = "MYSQL_USER"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "MYSQL_USER"
              }
            }
          }
          env {
            name = "MYSQL_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "MYSQL_PASSWORD"
              }
            }
          }

          volume_mount {
            name       = "mysql-data"
            mount_path = "/var/lib/mysql"
          }

          resources {
            requests = { cpu = "250m", memory = "512Mi" }
            limits   = { cpu = "1000m", memory = "1Gi" }
          }

          liveness_probe {
            exec {
              command = ["sh", "-c", "mysqladmin ping -h 127.0.0.1 -u root -p\"$${MYSQL_ROOT_PASSWORD}\""]
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          readiness_probe {
            exec {
              command = ["sh", "-c", "mysql -h 127.0.0.1 -u\"$${MYSQL_USER}\" -p\"$${MYSQL_PASSWORD}\" -e 'SELECT 1'"]
            }
            initial_delay_seconds = 20
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
          }
        }

        volume {
          name = "mysql-data"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.mysql.metadata[0].name
          }
        }
      }
    }
  }

  depends_on = [kubernetes_secret.auto_center]
}

resource "kubernetes_service" "mysql" {
  metadata {
    name      = "mysql-service"
    namespace = kubernetes_namespace.auto_center.metadata[0].name
    labels    = { app = "mysql", component = "database" }
  }

  spec {
    type     = "ClusterIP"
    selector = { app = "mysql" }

    port {
      name        = "mysql"
      port        = 3306
      target_port = 3306
      protocol    = "TCP"
    }
  }
}

# ──────────────────────────────────────────────────────────────────────────────
# APLICAÇÃO — Spring Boot
# ──────────────────────────────────────────────────────────────────────────────
resource "kubernetes_deployment" "app" {
  metadata {
    name      = "auto-center-fiap"
    namespace = kubernetes_namespace.auto_center.metadata[0].name
    labels    = { app = "auto-center-fiap", component = "api" }
  }

  spec {
    replicas = var.app_replicas

    selector {
      match_labels = { app = "auto-center-fiap" }
    }

    strategy {
      type = "RollingUpdate"
      rolling_update {
        max_surge       = "1"
        max_unavailable = "0"
      }
    }

    template {
      metadata {
        labels = { app = "auto-center-fiap", component = "api" }
      }

      spec {
        # ── Init Container 1: aguarda MySQL ───────────────────────
        init_container {
          name  = "wait-for-mysql"
          image = "busybox:1.36"
          command = ["sh", "-c", "until nc -z mysql-service 3306; do echo 'Aguardando MySQL...'; sleep 3; done; echo 'MySQL disponível!'"]
        }

        # ── Init Container 2: busca secrets no Infisical ──────────
        init_container {
          name  = "infisical-secrets-fetcher"
          image = "alpine:3.19"
          command = ["sh", "-c", <<-EOT
            set -e
            apk add --no-cache curl jq > /dev/null 2>&1
            echo "[Infisical] Autenticando..."
            ACCESS_TOKEN=$(curl -sf -X POST \
              "https://app.infisical.com/api/v1/auth/universal-auth/login" \
              -H "Content-Type: application/json" \
              -d "{\"clientId\":\"$${INFISICAL_CLIENT_ID}\",\"clientSecret\":\"$${INFISICAL_CLIENT_SECRET}\"}" \
              | jq -r '.accessToken')
            if [ -z "$ACCESS_TOKEN" ] || [ "$ACCESS_TOKEN" = "null" ]; then
              echo "[Infisical] ERRO: falha na autenticação."
              exit 1
            fi
            echo "[Infisical] Buscando secrets..."
            SECRETS_JSON=$(curl -sf \
              "https://app.infisical.com/api/v3/secrets/raw?workspaceId=$${INFISICAL_PROJECT_ID}&environment=$${INFISICAL_ENVIRONMENT}&secretPath=$${INFISICAL_SECRET_PATH}" \
              -H "Authorization: Bearer $ACCESS_TOKEN")
            mkdir -p /vault-secrets
            echo "$SECRETS_JSON" | jq -r '.secrets[] | "\(.secretKey)=\(.secretValue)"' > /vault-secrets/application.properties
            echo "[Infisical] Secrets gravados com sucesso."
          EOT
          ]

          env {
            name = "INFISICAL_CLIENT_ID"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "INFISICAL_CLIENT_ID"
              }
            }
          }
          env {
            name = "INFISICAL_CLIENT_SECRET"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "INFISICAL_CLIENT_SECRET"
              }
            }
          }
          env {
            name = "INFISICAL_PROJECT_ID"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "INFISICAL_PROJECT_ID"
              }
            }
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.auto_center.metadata[0].name
            }
          }

          volume_mount {
            name       = "vault-secrets"
            mount_path = "/vault-secrets"
          }
        }

        # ── Container principal: Spring Boot ───────────────────────
        container {
          name              = "auto-center-fiap"
          image             = var.app_image
          image_pull_policy = "IfNotPresent"

          port {
            container_port = 8097
            name           = "http"
            protocol       = "TCP"
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.auto_center.metadata[0].name
            }
          }

          # Credenciais do banco passadas diretamente como env vars (alta prioridade no Spring Boot,
          # sobrepõem qualquer valor vindo de arquivos de properties, inclusive os do Infisical)
          env {
            name = "SPRING_DATASOURCE_USERNAME"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "MYSQL_USER"
              }
            }
          }
          env {
            name = "SPRING_DATASOURCE_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.auto_center.metadata[0].name
                key  = "MYSQL_PASSWORD"
              }
            }
          }

          volume_mount {
            name       = "vault-secrets"
            mount_path = "/vault-secrets"
            read_only  = true
          }

          resources {
            requests = { cpu = "250m", memory = "512Mi" }
            limits   = { cpu = "1000m", memory = "1Gi" }
          }

          liveness_probe {
            tcp_socket {
              port = 8097
            }
            initial_delay_seconds = 60
            period_seconds        = 15
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          readiness_probe {
            http_get {
              path = "/swagger-ui.html"
              port = 8097
            }
            initial_delay_seconds = 40
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 5
          }

          startup_probe {
            tcp_socket {
              port = 8097
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            failure_threshold     = 12
          }
        }

        # ── Volume em RAM para secrets do Infisical ────────────────
        volume {
          name = "vault-secrets"
          empty_dir {
            medium = "Memory"
          }
        }

        affinity {
          pod_anti_affinity {
            preferred_during_scheduling_ignored_during_execution {
              weight = 100
              pod_affinity_term {
                label_selector {
                  match_expressions {
                    key      = "app"
                    operator = "In"
                    values   = ["auto-center-fiap"]
                  }
                }
                topology_key = "kubernetes.io/hostname"
              }
            }
          }
        }

        termination_grace_period_seconds = 30
      }
    }
  }

  depends_on = [
    kubernetes_deployment.mysql,
    kubernetes_service.mysql,
    kubernetes_config_map.auto_center,
    kubernetes_secret.auto_center,
  ]
}

resource "kubernetes_service" "app" {
  metadata {
    name      = "auto-center-fiap-service"
    namespace = kubernetes_namespace.auto_center.metadata[0].name
    labels    = { app = "auto-center-fiap", component = "api" }
  }

  spec {
    type     = "NodePort"
    selector = { app = "auto-center-fiap" }

    port {
      name        = "http"
      port        = 80
      target_port = 8097
      node_port   = 30080
      protocol    = "TCP"
    }
  }
}

# ──────────────────────────────────────────────────────────────────────────────
# HPA — Horizontal Pod Autoscaler
# ──────────────────────────────────────────────────────────────────────────────
resource "kubernetes_horizontal_pod_autoscaler_v2" "app" {
  metadata {
    name      = "auto-center-fiap-hpa"
    namespace = kubernetes_namespace.auto_center.metadata[0].name
    labels    = { app = "auto-center-fiap" }
  }

  spec {
    scale_target_ref {
      api_version = "apps/v1"
      kind        = "Deployment"
      name        = kubernetes_deployment.app.metadata[0].name
    }

    min_replicas = 2
    max_replicas = 10

    metric {
      type = "Resource"
      resource {
        name = "cpu"
        target {
          type                = "Utilization"
          average_utilization = 70
        }
      }
    }

    metric {
      type = "Resource"
      resource {
        name = "memory"
        target {
          type                = "Utilization"
          average_utilization = 80
        }
      }
    }

    behavior {
      scale_up {
        stabilization_window_seconds = 60
        select_policy                = "Max"
        policy {
          type           = "Pods"
          value          = 2
          period_seconds = 60
        }
        policy {
          type           = "Percent"
          value          = 50
          period_seconds = 60
        }
      }
      scale_down {
        stabilization_window_seconds = 300
        select_policy                = "Min"
        policy {
          type           = "Pods"
          value          = 1
          period_seconds = 120
        }
      }
    }
  }

  depends_on = [
    helm_release.metrics_server,
    kubernetes_deployment.app,
  ]
}

