# 🏗️ Auto Center FIAP — Infraestrutura com Terraform

O Terraform provisiona **toda** a infraestrutura automaticamente, incluindo:

- Cluster Kubernetes local (Kind) com 1 control-plane e 2 workers
- Build da imagem Docker da aplicacao
- Carga da imagem no cluster
- Metrics Server (para o HPA funcionar)
- Namespace, ConfigMap, Secrets
- MySQL 8.0 com PVC e health checks
- Spring Boot com init containers (Infisical + wait-for-mysql)
- HPA (escala de 2 a 10 replicas por CPU/memoria)

> Nenhum comando manual e necessario alem dos listados abaixo.

---

## O que o Terraform cria

| Recurso | Tipo | Descricao |
|---|---|---|
| Cluster Kind | null_resource (local-exec) | Cluster Kubernetes local com 1 control-plane e 2 workers |
| Imagem Docker | null_resource (local-exec) | Build + carga da imagem no cluster Kind |
| Metrics Server | helm_release | Necessario para o HPA funcionar |
| Namespace | kubernetes_namespace | Namespace `auto-center` |
| ConfigMap | kubernetes_config_map | Variaveis nao sensiveis da aplicacao |
| Secret | kubernetes_secret | Credenciais do MySQL e do Infisical |
| PVC | kubernetes_persistent_volume_claim | Volume de 10Gi para dados do MySQL |
| Deployment MySQL | kubernetes_deployment | MySQL 8.0 com health checks |
| Service MySQL | kubernetes_service | MySQL interno na porta 3306 |
| Deployment App | kubernetes_deployment | Spring Boot com init containers |
| Service App | kubernetes_service | API exposta na porta 30080 (NodePort) |
| HPA | kubernetes_horizontal_pod_autoscaler | Autoscaling 2-10 replicas |

---

## Pre-requisitos

Instale as ferramentas abaixo antes de comecar:

### Docker

```bash
sudo apt-get update && sudo apt-get install -y docker.io
sudo usermod -aG docker $USER
newgrp docker
docker --version
```

### Kind

```bash
curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.22.0/kind-linux-amd64
chmod +x ./kind && sudo mv ./kind /usr/local/bin/kind
kind --version
```

### kubectl

```bash
curl -LO "https://dl.k8s.io/release/$(curl -Ls https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl && sudo mv kubectl /usr/local/bin/kubectl
kubectl version --client
```

### Terraform

```bash
wget -O- https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
sudo apt update && sudo apt install -y terraform
terraform --version
```

---

## Configurar o Infisical (uma unica vez)

A aplicacao usa o [Infisical](https://app.infisical.com) para gerenciar secrets em producao.

1. Crie uma conta gratuita em https://app.infisical.com
2. Crie um projeto chamado `auto-center-fiap`
3. Va em **Access Control -> Machine Identities** e crie uma identity com **Universal Auth**
4. Copie o `Client ID` e gere um `Client Secret`
5. Va em **Project Settings** e copie o `Project ID`
6. Adicione os secrets no environment `prod`, path `/`:

```
SISTEMA_SEGURANCA_CHAVE_SECRETA = (gere com: openssl rand -base64 32)
```

---

## Subir a aplicacao (4 passos)

### 1. Configurar as variaveis

```bash
cd terraform/
cp terraform.tfvars.example terraform.tfvars
```

Edite o arquivo `terraform.tfvars` com seus valores reais:

```hcl
cluster_name = "auto-center"
app_image    = "auto-center-fiap:latest"
app_replicas = 2

mysql_root_password = "uma-senha-forte-root"
mysql_database      = "autocenter"
mysql_user          = "autocenter_user"
mysql_password      = "uma-senha-forte-app"

infisical_client_id     = "cole-seu-client-id"
infisical_client_secret = "cole-seu-client-secret"
infisical_project_id    = "cole-seu-project-id"
infisical_environment   = "prod"
infisical_secret_path   = "/"
```

> **Atencao:** Nunca commite o `terraform.tfvars` no Git. Ele ja esta no `.gitignore`.

### 2. Inicializar o Terraform

```bash
terraform init
```

### 3. Subir tudo

```bash
terraform apply -auto-approve
```

O Terraform executara automaticamente na ordem correta:

1. Cria o cluster Kind (1 control-plane + 2 workers)
2. Faz o build da imagem Docker da aplicacao
3. Carrega a imagem no cluster
4. Instala o Metrics Server via Helm
5. Cria todos os recursos Kubernetes (namespace, MySQL, app, HPA)

O processo leva de **8 a 15 minutos** na primeira vez (build Maven + Docker).

### 4. Acessar a aplicacao

```bash
kubectl port-forward service/auto-center-fiap-service 8080:80 -n auto-center
```

Abra no navegador:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Health Check:** http://localhost:8080/actuator/health

Credenciais padrao:

```
Usuario: ADMIN
Senha:   ADMIN
```

---

## Acompanhar os pods subindo

```bash
kubectl get pods -n auto-center -w
```

Aguarde todos ficarem com `STATUS Running` e `READY 1/1`.
Os init containers fazem: (1) aguardar MySQL e (2) buscar secrets do Infisical.
Isso pode levar de 2 a 4 minutos apos o apply.

---

## Remover tudo

O `terraform destroy` remove o cluster Kind e todos os recursos:

```bash
cd terraform/
terraform destroy -auto-approve
```

---

## Comandos uteis pos-deploy

```bash
# Ver todos os pods
kubectl get pods -n auto-center

# Ver logs da aplicacao
kubectl logs -l app=auto-center-fiap -n auto-center -f

# Ver status do autoscaler
kubectl get hpa -n auto-center

# Reiniciar a aplicacao
kubectl rollout restart deployment/auto-center-fiap -n auto-center

# Ver eventos do namespace (util para debug)
kubectl get events -n auto-center --sort-by='.lastTimestamp'
```

---

## Solucao de problemas

### Erro: cluster already exists

O cluster foi criado fora do Terraform. Delete-o e rode novamente:

```bash
kind delete cluster --name auto-center
terraform apply -auto-approve
```

### Docker parado

```bash
sudo systemctl start docker
terraform apply -auto-approve
```

### Pod em CrashLoopBackOff

```bash
kubectl logs <nome-do-pod> -n auto-center --previous
```

Verifique se as credenciais do Infisical no `terraform.tfvars` estao corretas.

### HPA mostrando "unknown" nas metricas

O Metrics Server pode ainda estar iniciando. Aguarde 2 minutos:

```bash
kubectl get hpa -n auto-center
kubectl get pods -n kube-system | grep metrics-server
```

### Forccar rebuild da imagem Docker

```bash
terraform taint null_resource.docker_build_and_load
terraform apply -auto-approve
```
