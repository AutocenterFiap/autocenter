📘 Projeto: API AutoCenter FIAP

🚀 Visão Geral

API REST para gerenciamento de clientes, veículos, serviços e produtos de uma oficina mecânica. Desenvolvida em Java 21 com Spring Boot 3.3.4, utilizando segurança OAuth2 com JWT e documentação via OpenAPI/Swagger.

🛠️ Tecnologias Utilizadas

Java 21

Spring Boot 3.3.4

Spring Data JPA

Spring Security (OAuth2 + JWT)

Flyway (migração de banco de dados)

H2 (dev) / MySQL (prod)

📂 Estrutura do Projeto

O projeto segue uma arquitetura em camadas, organizada nos pacotes:

br.com.autocenterfiap.domain
 ├── controller
 ├── dto
 ├── enums
 ├── exception
 ├── handler
 ├── mapper
 ├── model
 ├── repository
 ├── service
 └── validator

⚙️ Configuração e Execução

Pré-requisitos

JDK 21

Maven ou Gradle

Banco de dados configurado (H2 para dev, MySQL para prod)

Executando localmente

# Clonar repositório
git clone https://github.com/seuusuario/autocenter-fiap.git

# Entrar na pasta
cd autocenter-fiap

# Rodar com Maven
./mvnw spring-boot:run

🔑 Perfis (Profiles)

dev: usa H2 em memória e migrações em db/migration/dev

prod: usa MySQL e migrações em db/migration/prod

Ativar perfil:

./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

📖 Documentação da API

Após subir a aplicação:

Swagger UI: http://localhost:8097/swagger-ui/index.html

OpenAPI JSON: http://localhost:8097/v3/api-docs

🔐 Autenticação

A API utiliza OAuth2 com JWT.

Obtenha token em /v1/oauth/token.

Use o botão Authorize no Swagger UI ou envie o header:

Authorization: Bearer <seu_token>

🧪 Testes

Rodar testes:

./mvnw test

📌 Próximos Passos

Configurar CI/CD

Adicionar monitoramento

Melhorar documentação de endpoints