package br.com.autocenterfiap.security.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Value("${sistema.cache.expiracao.minutos}")
    private Integer tempoExpiracao;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("tokens");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(tempoExpiracao, TimeUnit.MINUTES)
                .maximumSize(1000)); // Limite de 100 tokens no cache para poupar memória
        return cacheManager;
    }
}

