package br.com.autocenterfiap.security.infrastructure.token;

import br.com.autocenterfiap.security.application.port.TokenPort;
import br.com.autocenterfiap.security.domain.exception.FalhaCriacaoTokenException;
import br.com.autocenterfiap.security.domain.exception.TokenInvalidoException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class Auth0TokenAdapter implements TokenPort {

    @Value("${sistema.seguranca.chave.secreta}")
    private String chaveSecreta;

    @Value("${sistema.token.expiracao.minutos}")
    private Integer tempoExpiracao;

    @Override
    @Cacheable(value = "tokens", key = "#username")
    public String gerarToken(String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(chaveSecreta);
            return JWT.create()
                    .withIssuer("Auto Center Fiap")
                    .withSubject(username)
                    .withExpiresAt(expiracao(tempoExpiracao))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new FalhaCriacaoTokenException("Erro ao gerar token JWT de acesso!", exception);
        }
    }

    @Override
    public String gerarRefreshToken(String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(chaveSecreta);
            return JWT.create()
                    .withIssuer("Auto Center Fiap")
                    .withSubject(username)
                    .withExpiresAt(expiracao(tempoExpiracao + 60))
                    .sign(algorithm);
        } catch (FalhaCriacaoTokenException exception) {
            throw new FalhaCriacaoTokenException("Erro ao gerar Refresh token JWT de acesso!", exception);
        }
    }

    @Override
    public String verificarToken(String token) {
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(chaveSecreta);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Auto Center Fiap")
                    .build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception) {
            throw new TokenInvalidoException("Erro ao verificar token JWT de acesso!", exception);
        }
    }

    // Método para "limpar" o cache (ex: no logout ou quando o token expirar)
    @Override
    @CacheEvict(value = "tokens", key = "#username")
    public void limparCache(String username) {
    }

    private Instant expiracao(Integer minutos) {
        return LocalDateTime.now().plusMinutes(minutos).toInstant(ZoneOffset.of("-03:00"));
    }
}
