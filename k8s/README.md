# ☸️ Kubernetes — Auto Center FIAP

Manifestos para deploy da aplicação **auto-center-fiap** em Kubernetes.

---

## 📁 Estrutura dos arquivos

```
k8s/
├── namespace.yaml          # Namespace "auto-center"
├── configmap.yaml          # Variáveis de configuração não-sensíveis
├── secret.yaml             # Variáveis sensíveis (DB password, JWT secret)
├── mysql-pvc.yaml          # PersistentVolumeClaim para o MySQL (10 Gi)
├── mysql-deployment.yaml   # Deployment do MySQL 8.0
├── mysql-service.yaml      # Service ClusterIP do MySQL (porta 3306)
├── app-deployment.yaml     # Deployment da API Spring Boot (porta 8097)
├── app-service.yaml        # Service LoadBalancer da API (porta 80)
├── hpa.yaml                # HorizontalPodAutoscaler (2–10 réplicas)
└── kustomization.yaml      # Orquestrador Kustomize
```

---

## ⚙️ Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| kubectl | 1.26+ |
| Kubernetes cluster | 1.26+ |
| Metrics Server | Instalado (para HPA) |
| Docker registry | Acessível pelo cluster |

### Instalar o Metrics Server (se necessário)
```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

---

## 🔐 Configurando os Secrets antes do deploy

Edite o arquivo `secret.yaml` e substitua os valores pelos dados reais do ambiente:

```yaml
stringData:
  SPRING_DATASOURCE_USERNAME: "autocenter_user"        # usuário do banco
  SPRING_DATASOURCE_PASSWORD: "SUA_SENHA_AQUI"         # senha do banco
  MYSQL_ROOT_PASSWORD: "SUA_SENHA_ROOT_AQUI"
  MYSQL_USER: "autocenter_user"
  MYSQL_PASSWORD: "SUA_SENHA_AQUI"
  SISTEMA_SEGURANCA_CHAVE_SECRETA: "CHAVE_JWT_256_BITS" # min. 32 chars
```

Para gerar uma chave JWT segura:
```bash
openssl rand -base64 32
```

---

## 🐳 Build e publicação da imagem Docker

```bash
# Na raiz do projeto (onde está o pom.xml)
docker build -t SEU_REGISTRY/auto-center-fiap:1.0.0 -f docker/Dockerfile .
docker push SEU_REGISTRY/auto-center-fiap:1.0.0
```

Atualize o campo `image` em `app-deployment.yaml`:
```yaml
image: SEU_REGISTRY/auto-center-fiap:1.0.0
```

---

## 🚀 Deploy

### Opção 1 — Kustomize (recomendado)
```bash
# Visualizar o que será aplicado
kubectl kustomize k8s/

# Aplicar tudo de uma vez
kubectl apply -k k8s/
```

### Opção 2 — Arquivo por arquivo (ordem obrigatória)
```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/mysql-pvc.yaml
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/mysql-service.yaml

# Aguarde o MySQL ficar pronto
kubectl wait --for=condition=ready pod -l app=mysql -n auto-center --timeout=120s

kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
kubectl apply -f k8s/hpa.yaml
```

---

## 🔍 Monitoramento

```bash
# Verificar todos os recursos no namespace
kubectl get all -n auto-center

# Acompanhar pods em tempo real
kubectl get pods -n auto-center -w

# Ver logs da aplicação
kubectl logs -l app=auto-center-fiap -n auto-center --tail=100 -f

# Verificar status do HPA
kubectl get hpa -n auto-center
kubectl describe hpa auto-center-fiap-hpa -n auto-center

# Verificar uso de recursos dos pods
kubectl top pods -n auto-center
```

---

## 📊 Autoscaling (HPA)

| Métrica | Gatilho (scale up) | Réplicas mín. | Réplicas máx. |
|---|---|---|---|
| CPU | > 70% | 2 | 10 |
| Memória | > 80% | 2 | 10 |

**Comportamento:**
- **Scale up:** aguarda 60s de estabilização, adiciona até 2 pods ou 50% por vez
- **Scale down:** aguarda 5 min de estabilização, remove 1 pod por vez a cada 2 min

---

## 🔗 Acesso à API

Após o deploy, obtenha o IP externo:
```bash
kubectl get service auto-center-fiap-service -n auto-center
```

Endpoints disponíveis:
- **Swagger UI:** `http://EXTERNAL_IP/swagger-ui/index.html`
- **API Docs:** `http://EXTERNAL_IP/api-docs`
- **Health:** `http://EXTERNAL_IP/actuator/health`

---

## 🗑️ Remoção

```bash
# Remove todos os recursos do namespace
kubectl delete namespace auto-center

# Ou com Kustomize
kubectl delete -k k8s/
```

