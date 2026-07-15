# ⚙️ Configuração do CI/CD — GitHub Actions

Este guia descreve tudo que precisa ser feito **antes do primeiro push** para que o pipeline funcione corretamente.

---

## 📋 Visão Geral do Pipeline

```
push (main / master / clean-architecture)
        │
        ▼
┌─────────────────────┐
│  🔨 Build e Testes  │  ← GitHub Actions (ubuntu-latest)
│  mvn clean verify   │
└────────┬────────────┘
         │ (somente push, não PR)
         ▼
┌──────────────────────────┐
│  🐳 Build Docker Image   │  ← Self-hosted runner (sua máquina)
│  docker build + load     │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────┐
│  🗄️ Deploy MySQL (K8s)   │  ← Self-hosted runner (sua máquina)
│  PVC + Deployment + Svc  │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────┐
│  🚀 Deploy Aplicação     │  ← Self-hosted runner (sua máquina)
│  kubectl apply -k k8s/   │
└──────────────────────────┘
```

---

## ✅ Pré-requisitos na sua máquina local

Certifique-se de que os seguintes programas estão instalados e funcionando:

### 1. Docker

```bash
# Verificar se está instalado
docker --version

# Instalar (Ubuntu/Debian)
sudo apt-get update
sudo apt-get install -y docker.io
sudo usermod -aG docker $USER   # adiciona seu usuário ao grupo docker
newgrp docker                   # aplica sem precisar sair da sessão
```

### 2. kubectl

```bash
# Verificar se está instalado
kubectl version --client

# Instalar
curl -LO "https://dl.k8s.io/release/$(curl -Ls https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

### 3. Minikube **ou** Kind (escolha um)

**Minikube** (recomendado para desenvolvimento local):
```bash
# Verificar
minikube version

# Instalar
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

# Iniciar o cluster
minikube start --driver=docker
```

**Kind** (alternativa mais leve):
```bash
# Verificar
kind version

# Instalar
curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.23.0/kind-linux-amd64
sudo install kind /usr/local/bin/kind

# Criar o cluster
kind create cluster --name auto-center
```

> **Confirmar que o cluster está rodando:**
> ```bash
> kubectl get nodes
> # NAME       STATUS   ROLES           AGE   VERSION
> # minikube   Ready    control-plane   ...
> ```

---

## 🤖 Passo 1 — Instalar o Self-Hosted Runner

O **self-hosted runner** é o agente que roda na sua máquina e executa os jobs de build Docker e deploy no Kubernetes.

### 1.1 Criar o runner no GitHub

1. Acesse o repositório no GitHub
2. Vá em **Settings → Actions → Runners**
3. Clique em **New self-hosted runner**
4. Selecione: **Linux** → **x64**
5. Siga os comandos exibidos na tela (copie diretamente do GitHub, pois o token muda a cada geração)

O fluxo será parecido com este:

```bash
# Criar diretório para o runner
mkdir -p ~/actions-runner && cd ~/actions-runner

# Baixar o runner (use a versão exibida pelo GitHub)
curl -o actions-runner-linux-x64-<VERSAO>.tar.gz -L \
  https://github.com/actions/runner/releases/download/v<VERSAO>/actions-runner-linux-x64-<VERSAO>.tar.gz

# Extrair
tar xzf ./actions-runner-linux-x64-<VERSAO>.tar.gz

# Configurar (use o token gerado pelo GitHub — ele expira em ~1h)
./config.sh --url https://github.com/<SEU_USUARIO>/<SEU_REPOSITORIO> --token <TOKEN_GERADO>
```

### 1.2 Instalar como serviço (para iniciar automaticamente)

```bash
cd ~/actions-runner

# Instalar o serviço systemd
sudo ./svc.sh install

# Iniciar o serviço
sudo ./svc.sh start

# Verificar o status
sudo ./svc.sh status
```

O runner aparecerá como **Idle** (verde) em **Settings → Actions → Runners** quando estiver pronto.

> **Parar o serviço manualmente (se necessário):**
> ```bash
> sudo ./svc.sh stop
> ```

---

## 🔐 Passo 2 — Configurar os GitHub Secrets

Os secrets são variáveis sensíveis (senhas, tokens) que o pipeline usa durante o deploy.

### 2.1 Acessar a tela de secrets

```
GitHub → Repositório → Settings → Secrets and variables → Actions → New repository secret
```

### 2.2 Secrets obrigatórios

Adicione **todos** os secrets abaixo:

| Nome do Secret | Descrição | Valor de exemplo |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | Senha do usuário `root` do MySQL | `MinhaS3nhaRoot!` |
| `MYSQL_DATABASE` | Nome do banco de dados | `autocenter` |
| `MYSQL_USER` | Usuário da aplicação no MySQL | `autocenter_user` |
| `MYSQL_PASSWORD` | Senha do usuário da aplicação | `MinhaS3nhaApp!` |
| `INFISICAL_CLIENT_ID` | Client ID da Machine Identity no Infisical | `901dfdbe-xxxx-xxxx` |
| `INFISICAL_CLIENT_SECRET` | Client Secret da Machine Identity no Infisical | `0c4c8c78xxxx` |
| `INFISICAL_PROJECT_ID` | Project ID do projeto no Infisical | `2e2e0833-xxxx-xxxx` |

> ⚠️ **Nunca** coloque esses valores diretamente no código ou no `secret.yaml` versionado.

### 2.3 Como obter as credenciais do Infisical

1. Acesse [app.infisical.com](https://app.infisical.com) e faça login
2. Crie um projeto chamado `auto-center-fiap` (se não existir)
3. Vá em **Project Settings → Identities → Add Machine Identity**
4. Escolha **Universal Auth** → copie o `Client ID` e `Client Secret`
5. O `Project ID` está em **Project Settings → Project ID**
6. No Infisical, adicione os secrets abaixo no environment `prod`, path `/`:

| Chave | Descrição |
|---|---|
| `SPRING_DATASOURCE_USERNAME` | mesmo valor de `MYSQL_USER` |
| `SPRING_DATASOURCE_PASSWORD` | mesmo valor de `MYSQL_PASSWORD` |
| `SISTEMA_SEGURANCA_CHAVE_SECRETA` | chave JWT aleatória: `openssl rand -base64 32` |

---

## 🔍 Passo 3 — Verificar tudo antes do push

Execute este checklist na sua máquina local:

```bash
# 1. Docker funcionando
docker info | grep "Server Version"

# 2. Cluster Kubernetes rodando
kubectl get nodes

# 3. Runner do GitHub ativo
# (verificar na tela Settings → Actions → Runners — deve aparecer "Idle")

# 4. Testar o build Maven localmente
./mvnw clean verify

# 5. Testar o build Docker localmente
docker build -f docker/Dockerfile -t auto-center-fiap:teste .
docker images | grep auto-center-fiap

# 6. Testar o acesso ao cluster
kubectl get namespaces
```

---

## 🚀 Passo 4 — Fazer o push e acompanhar o pipeline

```bash
git add .
git commit -m "feat: configuração do CI/CD"
git push origin main   # ou master / clean-architecture
```

Acompanhe a execução em:
```
GitHub → Repositório → Actions
```

### Ordem esperada de execução:

| # | Job | Runner | Duração estimada |
|---|-----|--------|-----------------|
| 1 | 🔨 Build e Testes | GitHub (ubuntu-latest) | ~3–5 min |
| 2 | 🐳 Build Docker | Self-hosted (local) | ~2–4 min |
| 3 | 🗄️ Deploy MySQL | Self-hosted (local) | ~1–3 min |
| 4 | 🚀 Deploy Aplicação | Self-hosted (local) | ~2–5 min |

---

## 🐛 Solução de Problemas

### Runner não aparece no GitHub

```bash
# Verificar logs do serviço
sudo journalctl -u actions.runner.* -f

# Reiniciar o serviço
cd ~/actions-runner
sudo ./svc.sh stop
sudo ./svc.sh start
```

### Job falha com "permission denied" no Docker

```bash
# Adicionar o usuário do runner ao grupo docker
sudo usermod -aG docker $USER
# Reiniciar o runner
sudo ./svc.sh stop && sudo ./svc.sh start
```

### `kubectl` não encontra o cluster

```bash
# Minikube
minikube start
kubectl config use-context minikube

# Kind
kind get clusters
kubectl config use-context kind-auto-center
```

### Imagem não encontrada no cluster (`ErrImagePull`)

```bash
# Minikube: recarregar a imagem
minikube image load auto-center-fiap:latest

# Kind: recarregar a imagem
kind load docker-image auto-center-fiap:latest --name auto-center
```

### Secret inválido ou expirado

```bash
# Verificar secrets no cluster
kubectl get secret auto-center-secrets -n auto-center -o yaml

# Recriar manualmente para testar
kubectl delete secret auto-center-secrets -n auto-center
```

---

## 📂 Estrutura dos arquivos de CI/CD

```
.github/
└── workflows/
    ├── ci-cd.yml    ← Pipeline principal
    └── README.md    ← Este arquivo
k8s/
├── namespace.yaml
├── configmap.yaml
├── secret.yaml      ← NÃO versionar com valores reais
├── mysql-pvc.yaml
├── mysql-deployment.yaml
├── mysql-service.yaml
├── app-deployment.yaml
├── app-service.yaml
├── hpa.yaml
└── kustomization.yaml
```

---

## 📞 Referências

- [GitHub Actions — Self-hosted runners](https://docs.github.com/en/actions/hosting-your-own-runners/managing-self-hosted-runners/about-self-hosted-runners)
- [Minikube — Getting Started](https://minikube.sigs.k8s.io/docs/start/)
- [Kind — Quick Start](https://kind.sigs.k8s.io/docs/user/quick-start/)
- [Infisical — Machine Identity](https://infisical.com/docs/documentation/platform/identities/machine-identities)
- [kubectl — Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)

