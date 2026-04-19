package br.com.autocenterfiap.security.config;

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

                            req.requestMatchers("/token", "/refresh-token").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/usuarios/permitido").permitAll();
//                            req.requestMatchers(HttpMethod.GET, "/usuarios/permitido").authenticated();

                            req.requestMatchers(HttpMethod.GET, "/usuarios/{nome}").permitAll();
                            req.requestMatchers(HttpMethod.POST, "/usuarios").hasAnyRole("ADMIN");
                            req.requestMatchers(HttpMethod.PATCH, "/usuarios/senha/alteracao").hasRole("ADMIN");


                            req.requestMatchers(HttpMethod.GET, "/v1/api/clientes/**").permitAll();
                            req.requestMatchers(HttpMethod.POST, "/v1/api/clientes").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.PUT, "/v1/api/clientes/{idCliente}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/v1/api/clientes/{idCliente}").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/veiculos").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/veiculos/{idVeiculo}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/veiculos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/veiculos/{idVeiculo}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.GET, "/veiculos/placa/{placa}").hasAnyRole("ADMIN","READ");
                            req.requestMatchers(HttpMethod.DELETE, "/veiculos/{idVeiculo}").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/servicos").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/servicos/{idServico}").hasAnyRole("ADMIN", "READ");
                            req.requestMatchers(HttpMethod.POST, "/servicos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/servicos/{idServico}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/servicos/{idServico}").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/produtos").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/produtos/{idProduto}").permitAll();
                            req.requestMatchers(HttpMethod.POST, "/produtos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/produtos/{idProduto}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/produtos/{idProduto}").hasAnyRole("ADMIN", "WRITE");

                            req.requestMatchers(HttpMethod.GET, "/ordem-servicos").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/ordem-servicos/{idOrdemServico}").permitAll();
                            req.requestMatchers(HttpMethod.POST, "/ordem-servicos").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.PUT, "/ordem-servicos/{idOrdemServico}").hasAnyRole("ADMIN", "WRITE");
                            req.requestMatchers(HttpMethod.DELETE, "/ordem-servicos/{idOrdemServico}").hasAnyRole("ADMIN", "WRITE");

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
