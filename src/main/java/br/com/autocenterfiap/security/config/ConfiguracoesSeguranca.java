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
                .authorizeHttpRequests(
                        req -> {
                            req.requestMatchers("/token", "/refresh-token").permitAll();
//                            req.requestMatchers(HttpMethod.GET, "/").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/").authenticated();

                            req.requestMatchers(HttpMethod.GET, "/api/clientes/**").permitAll();
                            req.requestMatchers(HttpMethod.POST, "/api/clientes").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.PUT, "/api/clientes/{idCliente}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.DELETE, "/api/clientes/{idCliente}").hasRole("ADMIN");

                            req.requestMatchers(HttpMethod.GET, "/veiculos").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/veiculos/{idVeiculo}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.POST, "/veiculos").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.PUT, "/veiculos/{idVeiculo}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.PUT, "/veiculos/placa/{placa}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.DELETE, "/veiculos/{idVeiculo}").hasRole("ADMIN");

                            req.requestMatchers(HttpMethod.GET, "/servicos").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/servicos/{idServico}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.GET, "/servicos/{idServico}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.POST, "/servicos").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.PUT, "/servicos/{idServico}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.DELETE, "/servicos/{idServico}").hasRole("ADMIN");

                            req.requestMatchers(HttpMethod.GET, "/produtos").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/produtos/{idProduto}").permitAll();
                            req.requestMatchers(HttpMethod.POST, "/produtos").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.PUT, "/produtos/{idProduto}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.DELETE, "/produtos/{idProduto}").hasRole("ADMIN");

                            req.requestMatchers(HttpMethod.GET, "/ordem-servicos").permitAll();
                            req.requestMatchers(HttpMethod.GET, "/ordem-servicos/{idOrdemServico}").permitAll();
                            req.requestMatchers(HttpMethod.POST, "/ordem-servicos").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.PUT, "/ordem-servicos/{idOrdemServico}").hasRole("ADMIN");
                            req.requestMatchers(HttpMethod.DELETE, "/ordem-servicos/{idOrdemServico}").hasRole("ADMIN");

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
