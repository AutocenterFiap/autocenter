# terraform (workspace "aplicacao")

Workspace Terraform Cloud (organizacao `autocenter-fiap`, workspace
`aplicacao`) que implanta a aplicacao `autocenter` no cluster EKS ja
existente (`eks-autocenter-fiap-infraestrutura`, criado pelo repositorio
`infraestrutura`), conectando ao RDS MySQL ja existente (criado pelo
repositorio `database`).

Este workspace **nao cria** VPC, subnets, EKS ou RDS — apenas le esses
recursos via `data` sources e remote state, e cria os recursos Kubernetes
da aplicacao (namespace `autocenter`, secret, configmap, deployment,
service, hpa).

## Configuracao do workspace no Terraform Cloud

1. Acesse a organizacao `autocenter-fiap` no Terraform Cloud.
2. Crie o workspace `aplicacao`.
3. Conecte este repositorio ao workspace, com "Terraform Working
   Directory" apontando para `terraform/`.
4. Confirme que os workspaces `infraestrutura` e `database` existem na
   mesma organizacao.
5. Em **Variables**, adicione (categoria Terraform):

   | Nome | Sensivel | Observacao |
   | --- | --- | --- |
   | `db_username` | Sim | Deve ser igual ao `db_username` do workspace `database` |
   | `db_password` | Sim | Deve ser igual ao `db_password` do workspace `database` |
   | `jwt_secret` | Sim | Valor de `sistema.seguranca.chave.secreta` em producao |
   | `dd_api_key` | Sim | API Key da organizacao Datadog (ver secao "Datadog" abaixo) |
   | `dd_site` | Nao | Site do Datadog (padrao `datadoghq.com`; use `us5.datadoghq.com`, `datadoghq.eu` etc. conforme a regiao da conta) |

   A variavel `app_image` **nao** deve ser cadastrada manualmente — ela e
   passada pelo pipeline de CI/CD (`terraform apply -var="app_image=..."`)
   a cada deploy.

6. Nao crie nem versione arquivo `terraform.tfvars` neste diretorio.

## Validacao local obrigatoria

Execute a partir deste diretorio antes de qualquer execucao remota:

```bash
terraform fmt -check
terraform init
terraform validate
```

## Saida esperada

Apos o apply, o workspace expoe `app_service_hostname` — hostname do
LoadBalancer para acessar `/actuator/health`, `/swagger-ui/index.html` etc.

## Datadog

O `datadog.tf` instala o Datadog Agent (DaemonSet, via Helm chart oficial
`datadog/datadog`) no namespace `autocenter`, habilitando:

- **Logs** dos containers (`containerCollectAll`), com injecao automatica
  de `dd.trace_id`/`dd.span_id` nos logs da aplicacao (`DD_LOGS_INJECTION`).
- **APM** (traces).
- **Metricas Prometheus/OpenMetrics** via autodiscovery, usando as
  anotacoes `ad.datadoghq.com/autocenter-fiap.checks` presentes no
  Deployment (scrape de `/actuator/prometheus`).

O Cluster Agent e o Process Agent estao desabilitados para reduzir o
consumo de CPU/memoria nos nodes `t3.micro`/`t3.small` do EKS.

Veja a raiz do repositorio (`README.md` ou secao "Configurar a conta
Datadog") para o passo a passo de criacao da API Key.
