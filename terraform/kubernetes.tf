resource "kubernetes_namespace" "autocenter" {
  metadata {
    name = "autocenter"
    labels = {
      app        = "autocenter-fiap"
      managed-by = "terraform"
    }
  }
}

resource "kubernetes_secret" "autocenter" {
  metadata {
    name      = "autocenter-secrets"
    namespace = kubernetes_namespace.autocenter.metadata[0].name
  }

  data = {
    SPRING_DATASOURCE_USERNAME      = var.db_username
    SPRING_DATASOURCE_PASSWORD      = var.db_password
    SISTEMA_SEGURANCA_CHAVE_SECRETA = var.jwt_secret
  }

  type = "Opaque"
}

resource "kubernetes_config_map" "autocenter" {
  metadata {
    name      = "autocenter-configmap"
    namespace = kubernetes_namespace.autocenter.metadata[0].name
  }

  data = {
    SPRING_PROFILES_ACTIVE = "prod"
    SERVER_PORT            = "8097"

    SPRING_DATASOURCE_URL               = "jdbc:mysql://${data.terraform_remote_state.database.outputs.rds_endpoint}:${data.terraform_remote_state.database.outputs.rds_port}/${var.db_name}?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true"
    SPRING_DATASOURCE_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver"

    SPRING_JPA_DATABASE_PLATFORM               = "org.hibernate.dialect.MySQLDialect"
    SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL = "false"
    SPRING_JPA_HIBERNATE_DDL_AUTO              = "validate"
    SPRING_JPA_SHOW_SQL                        = "false"

    SPRING_FLYWAY_ENABLED             = "true"
    SPRING_FLYWAY_BASELINE_ON_MIGRATE = "true"

    SISTEMA_TOKEN_EXPIRACAO_MINUTOS = "30"

    LOGGING_LEVEL_ROOT                  = "INFO"
    LOGGING_LEVEL_BR_COM_AUTOCENTERFIAP = "INFO"
  }
}

resource "kubernetes_deployment" "autocenter_app" {
  metadata {
    name      = "autocenter-app"
    namespace = kubernetes_namespace.autocenter.metadata[0].name
    labels = {
      app       = "autocenter-fiap"
      component = "api"
    }
  }

  spec {
    replicas = var.app_replicas

    selector {
      match_labels = {
        app = "autocenter-fiap"
      }
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
        labels = {
          app       = "autocenter-fiap"
          component = "api"
        }
      }

      spec {
        container {
          name              = "autocenter-fiap"
          image             = var.app_image
          image_pull_policy = "IfNotPresent"

          port {
            container_port = 8097
            name           = "http"
            protocol       = "TCP"
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.autocenter.metadata[0].name
            }
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.autocenter.metadata[0].name
            }
          }

          resources {
            requests = {
              cpu    = "100m"
              memory = "256Mi"
            }
            limits = {
              cpu    = "500m"
              memory = "400Mi"
            }
          }

          liveness_probe {
            http_get {
              path = "/actuator/health/liveness"
              port = 8097
            }
            initial_delay_seconds = 60
            period_seconds        = 15
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          readiness_probe {
            http_get {
              path = "/actuator/health/readiness"
              port = 8097
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          startup_probe {
            http_get {
              path = "/actuator/health"
              port = 8097
            }
            initial_delay_seconds = 20
            period_seconds        = 10
            failure_threshold     = 12
          }
        }

        termination_grace_period_seconds = 30
      }
    }
  }
}

resource "kubernetes_service" "autocenter_app" {
  metadata {
    name      = "autocenter-app-service"
    namespace = kubernetes_namespace.autocenter.metadata[0].name
    labels = {
      app       = "autocenter-fiap"
      component = "api"
    }
  }

  spec {
    type = "LoadBalancer"

    selector = {
      app = "autocenter-fiap"
    }

    port {
      name        = "http"
      port        = 80
      target_port = 8097
      protocol    = "TCP"
    }
  }
}

resource "kubernetes_horizontal_pod_autoscaler_v2" "autocenter_app" {
  metadata {
    name      = "autocenter-app-hpa"
    namespace = kubernetes_namespace.autocenter.metadata[0].name
  }

  spec {
    scale_target_ref {
      api_version = "apps/v1"
      kind        = "Deployment"
      name        = kubernetes_deployment.autocenter_app.metadata[0].name
    }

    min_replicas = 1
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
  }
}
