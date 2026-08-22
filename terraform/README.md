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
