<h1 align="center">
  🚗 API AutoCenter FIAP
</h1>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen?style=for-the-badge&logo=spring-boot">
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-Blue?style=for-the-badge&logo=mysql">
  <img alt="Kubernetes" src="https://img.shields.io/badge/Kubernetes-Kind-326CE5?style=for-the-badge&logo=kubernetes">
  <img alt="Terraform" src="https://img.shields.io/badge/Terraform-1.6+-7B42BC?style=for-the-badge&logo=terraform">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge">
</p>

## 🚀 Visão Geral

A **API AutoCenter FIAP** é um sistema RESTful para gerenciamento completo de uma oficina mecânica — clientes, veículos, serviços e produtos — com segurança **OAuth2/JWT** e documentação interativa via **Swagger**.

A infraestrutura roda localmente em **Kubernetes (Kind)** e é provisionada 100% via **Terraform**: cluster, build Docker, carga da imagem e todos os recursos Kubernetes são criados automaticamente com um único comando.

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.3.4 |
| Banco de Dados | MySQL 8.0 (prod) / H2 (dev) |
| Persistência | Spring Data JPA + Flyway |
| Segurança | Spring Security (OAuth2 + JWT) |
| Secrets | Infisical (gerenciador de secrets) |
| Orquestração | Kubernetes (Kind) |
| IaC | Terraform |
| Documentação | Springdoc OpenAPI (Swagger) |

---

## 📂 Estrutura do Projeto

```text
auto-center-fiap/
├── src/                    # Código-fonte Java (Clean Architecture)
├── docker/                 # Dockerfile e docker-compose
├── k8s/                    # Manifestos Kubernetes (alternativa ao Terraform)
├── terraform/              # Infraestrutura como código (recomendado)
│   ├── main.tf             # Cluster Kind + build Docker (via local-exec)
│   ├── kubernetes.tf       # Recursos Kubernetes (namespace, MySQL, app, HPA)
│   ├── variables.tf        # Variáveis de entrada
│   ├── outputs.tf          # Saídas úteis pós-deploy
│   └── terraform.tfvars.example  # Modelo de configuração
└── db/migration/           # Scripts Flyway
```

---

## ⚡ Subir a aplicação (Terraform — recomendado)

> **Pré-requisitos:** Docker, Kind, kubectl e Terraform instalados.
> Consulte [terraform/README.md](terraform/README.md) para instruções de instalação.

### 1. Configurar variáveis

```bash
cd terraform/
cp terraform.tfvars.example terraform.tfvars
# Edite terraform.tfvars com suas credenciais do MySQL e Infisical
```

### 2. Inicializar o Terraform

```bash
terraform init
```

### 3. Subir tudo

```bash
terraform apply -auto-approve
```

O Terraform cria o cluster Kind, faz o build Docker, carrega a imagem e sobe todos os recursos Kubernetes automaticamente. Aguarde de **8 a 15 minutos** na primeira execução.

### 4. Acessar a aplicação

```bash
kubectl port-forward service/auto-center-fiap-service 8080:80 -n auto-center
```

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Health Check:** http://localhost:8080/actuator/health

### 5. Remover tudo

```bash
terraform destroy -auto-approve
```

---

## 🖥️ Rodar Localmente (sem Kubernetes)

Para desenvolvimento rápido com H2 em memória:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

A API estará em `http://localhost:8097`.

Para rodar com MySQL via Docker Compose:

```bash
cd docker/
docker-compose up -d
```

---

## 🔑 Autenticação

A API usa OAuth2 com JWT. Para obter um token:

```bash
curl -X POST http://localhost:8080/v1/oauth/token \
  -H "Content-Type: application/json" \
  -d '{"nome": "ADMIN", "senha": "ADMIN"}'
```

Use o token retornado no header:
```
Authorization: Bearer <token>
```

Ou clique em **Authorize** no Swagger UI.

---

## 📖 Documentação

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Terraform (infra):** [terraform/README.md](terraform/README.md)
- **Kubernetes (manifests):** [k8s/README.md](k8s/README.md)
- **Diagramas DDD:** https://miro.com/app/board/uXjVGxJbbQU=/

---

## 🧪 Testes

```bash
./mvnw test
```
