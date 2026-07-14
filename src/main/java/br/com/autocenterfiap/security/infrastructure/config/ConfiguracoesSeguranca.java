package br.com.autocenterfiap.security.infrastructure.config;

import br.com.autocenterfiap.security.infrastructure.security.FiltroTokenAcesso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracoesSeguranca {

    private final FiltroTokenAcesso filtroTokenAcesso;

    public ConfiguracoesSeguranca(FiltroTokenAcesso filtroTokenAcesso) {
        this.filtroTokenAcesso = filtroTokenAcesso;
    }

    @Bean
    public SecurityFilterChain filtrosSeguranca(HttpSecurity http) throws Exception {
        return http
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(
                        req -> {
                             req.requestMatchers("/h2-console/**").permitAll();// Libera o console H2

                            req.requestMatchers("/v1/oauth/token", "/v1/oauth/refresh-token").permitAll();

                            req.requestMatchers(HttpMethod.GET, "/v1/usuarios/{nome}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/usuarios").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PATCH, "/v1/usuarios/senha/alteracao").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/v1/orcamentos").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.GET, "/v1/orcamentos/{id}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/orcamentos/{id}/enviar-cliente").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PATCH, "/v1/orcamentos/{id}/aprovar").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PATCH, "/v1/orcamentos/{id}/reprovar").hasAnyRole("ADMIN", "WRITE");


                            req.requestMatchers(HttpMethod.GET, "/v1/clientes/**").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/clientes").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/v1/clientes/{idCliente}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/v1/clientes/{idCliente}").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/v1/veiculos").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.GET, "/v1/veiculos/{idVeiculo}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/veiculos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/v1/veiculos/{idVeiculo}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.GET, "/v1/veiculos/placa/{placa}").hasAnyRole("ADMIN","READ");
                            req.requestMatchers(HttpMethod.DELETE, "/v1/veiculos/{idVeiculo}").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/v1/servicos").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.GET, "/v1/servicos/{idServico}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/servicos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/v1/servicos/{idServico}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/v1/servicos/{idServico}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.GET, "/v1/servicos/status/{status}").hasAnyRole("ADMIN", "READ");

                            req.requestMatchers(HttpMethod.GET, "/v1/ordem-servicos").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.GET, "/v1/ordem-servicos/{idOrdemServico}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.GET, "/v1/ordem-servicos/numero/{numeroOrdemServico}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/ordem-servicos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PATCH, "/v1/ordem-servicos/{idOrdemServico}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/v1/ordem-servicos/{idOrdemServico}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.GET, "/v1/ordem-servico/metricas/servicos").hasAnyRole("ADMIN", "READ");

                            req.requestMatchers(HttpMethod.GET, "/v1/produtos").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.GET, "/v1/produtos/{id}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.GET, "/v1/produtos/estoque/alertas").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/produtos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/v1/produtos/{id}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/v1/produtos/{id}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PATCH, "/v1/produtos/{id}/estoque/adicionar").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PATCH, "/v1/produtos/{id}/estoque/remover").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/v1/ordem-servicos/{osId}/produtos").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/ordem-servicos/{osId}/produtos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/v1/ordem-servicos/{osId}/produtos/{produtoId}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/v1/ordem-servicos/{osId}/produtos/{produtoId}").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/v1/ordem-servico/{ordemServicoId}/servicos").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/v1/ordem-servico/{ordemServicoId}/servicos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PATCH, "/v1/ordem-servico/{ordemServicoId}/servicos/{servicoId}/iniciar").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PATCH, "/v1/ordem-servico/{ordemServicoId}/servicos/{servicoId}/finalizar").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/v1/ordem-servico/{ordemServicoId}/servicos/{servicoId}").hasAnyRole("ADMIN", "WRITE");

                            // Swagger - público para documentação
                            req.requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll();
                            req.requestMatchers("/v3/api-docs/**", "/api-docs/**").permitAll();
                            req.requestMatchers("/swagger-resources/**", "/webjars/**").permitAll();

                            // H2 Console - público (apenas para desenvolvimento)
                            req.requestMatchers("/h2-console/**").permitAll();

                            req.anyRequest().authenticated();
                        }
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Para H2 Console
                .addFilterBefore(filtroTokenAcesso, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder encriptador() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
