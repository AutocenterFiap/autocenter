package br.com.autocenterfiap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI para documentação da API.
 *
 * Acesse a documentação em:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs (JSON): http://localhost:8080/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auto Center FIAP - API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de oficina automotiva. " +
                                "Sistema para cadastro de clientes, veículos e serviços.")
                        .contact(new Contact()
                                .name("Equipe Auto Center FIAP")
                                .email("contato@autocenterfiap.com.br")
                                .url("https://github.com/autocenterfiap"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}
