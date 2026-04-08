package br.com.autocenterfiap.security.service;

import br.com.autocenterfiap.security.exception.FalhaCriacaoTokenException;
import br.com.autocenterfiap.security.exception.TokenInvalidoException;
import br.com.autocenterfiap.security.model.Usuario;
import br.com.autocenterfiap.security.exception.RegraDeNegocioException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${sistema.seguranca.chave.secreta}")
    private String chaveSecreta;

    public String gerarToken(Usuario usuario){
        try {
            Algorithm algorithm = Algorithm.HMAC256(chaveSecreta);
            return JWT.create()
                    .withIssuer("Auto Center Fiap")
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(expiracao(30))
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new FalhaCriacaoTokenException("Erro ao gerar token JWT de acesso!", exception);
        }
    }

    public String gerarRefreshToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(chaveSecreta);
            return JWT.create()
                    .withIssuer("Auto Center Fiap")
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(expiracao(120))
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new FalhaCriacaoTokenException("Erro ao gerar Refresh token JWT de acesso!", exception);
        }
    }

    public String verificarToken(String token){
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(chaveSecreta);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Auto Center Fiap")
                    .build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception){
            throw new TokenInvalidoException("Erro ao verificar token JWT de acesso!", exception);
        }
    }

    private Instant expiracao(Integer minutos) {
        return LocalDateTime.now().plusMinutes(minutos).toInstant(ZoneOffset.of("-03:00"));
    }
}
