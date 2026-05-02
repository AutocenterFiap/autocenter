<h1 align="center">
  🚗 API AutoCenter FIAP
</h1>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen?style=for-the-badge&logo=spring-boot">
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-Blue?style=for-the-badge&logo=mysql">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge">
</p>

## 🚀 Visão Geral

A **API AutoCenter FIAP** é um sistema RESTful desenvolvido para o gerenciamento completo de uma oficina mecânica. Ela permite controlar clientes, veículos, serviços e produtos, provendo uma base sólida para as operações diárias de um Auto Center. 

A aplicação foi construída com foco em boas práticas, utilizando **Java 21**, **Spring Boot 3.3.4**, e garantindo segurança através de **OAuth2 com JWT**. Toda a API possui documentação interativa via **OpenAPI/Swagger**.

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.3.4
- **Persistência:** Spring Data JPA, Flyway (Migrações)
- **Banco de Dados:** H2 Database (Desenvolvimento) / MySQL (Produção)
- **Segurança:** Spring Security (OAuth2 + JWT)
- **Documentação:** Springdoc OpenAPI (Swagger)

> *"Optamos pelo MySQL porque é mais simples de configurar e tem suporte nativo em diversas ferramentas que utilizamos no curso. Isso nos permitiu focar na implementação da arquitetura em camadas, segurança com OAuth2/JWT e documentação com Swagger, sem gastar tempo excessivo em ajustes de banco. Além disso, o MySQL é amplamente usado em ambientes acadêmicos e corporativos, o que facilita encontrar suporte e exemplos."*

## 📂 Estrutura do Projeto

O projeto segue uma arquitetura baseada em camadas, organizada da seguinte forma:

```text
br.com.autocenterfiap.domain
┌─ controller  # Endpoints REST
├─ dto         # Objetos de Transferência de Dados
├─ enums       # Enumerações
├─ exception   # Tratamento global de erros
├─ handler     # Manipuladores de exceção
├─ mapper      # Conversão entre DTOs e Models
├─ model       # Entidades JPA
├─ repository  # Interfaces de acesso a dados
├─ service     # Lógica de negócio
└─ validator   # Validações customizadas
```

## ⚙️ Como Rodar Localmente

É muito simples rodar o projeto na sua máquina. Siga os passos abaixo:

### Pré-requisitos
- JDK 21 instalado
- Git
- (Opcional) Docker e Docker Compose para rodar com banco MySQL local

### Passo a Passo (H2 Database - Memória)

A forma mais rápida de rodar é utilizando o perfil de desenvolvimento (`dev`), que sobe um banco H2 em memória automaticamente.

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/liamfer/autocenter-fiap.git
   cd autocenter-fiap
   ```

2. **Execute a aplicação usando o Maven Wrapper:**
   ```bash
   # No Windows
   mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

   # No Linux/Mac
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. Pronto! A API estará rodando em `http://localhost:8097`.

### Rodando com Docker (MySQL)
Se quiser rodar a aplicação simulando o ambiente de produção com MySQL utilizando os contêineres já configurados:

```bash
cd docker
docker-compose up -d
```

## 🔑 Autenticação e Login de Admin

A API é protegida utilizando o padrão OAuth2 com JWT. Para testar os endpoints restritos, você precisará gerar um token de acesso usando as credenciais de administrador padrão.

**Credenciais de Acesso (Admin):**
```json
{
  "nome": "ADMIN",
  "senha": "ADMIN"
}
```

**Como Autenticar:**
1. Faça uma requisição `POST` para o endpoint de login (`/v1/oauth/token`) enviando um JSON com as credenciais acima.
2. Copie o token JWT retornado na resposta.
3. Se estiver usando o Swagger UI, clique no botão **Authorize** e insira o token, ou adicione o seguinte Header nas suas requisições:
   ```text
   Authorization: Bearer <seu_token_jwt>
   ```

## 📖 Documentação da API

Com a aplicação rodando localmente, você pode acessar a documentação interativa e testar os endpoints diretamente pelo navegador:

- **Swagger UI:** [http://localhost:8097/swagger-ui/index.html](http://localhost:8097/swagger-ui/index.html)
- **OpenAPI JSON:** [http://localhost:8097/api-docs](http://localhost:8097/v3/api-docs)

## 🥢 Testes

A aplicação conta com uma robusta suíte de testes unitários e de integração. Para executá-los:

```bash
# No Windows
mvnw.cmd test

# No Linux/Mac
./mvnw test
```

## 📌 Próximos Passos

- [ ] Configurar CI/CD (GitHub Actions)
- [ ] Adicionar monitoramento (Actuator + Prometheus + Grafana)
- [ ] Melhorar ainda mais a cobertura de testes (JaCoCo)
