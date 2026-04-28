package br.com.autocenterfiap.security.service;

import br.com.autocenterfiap.security.exception.TokenInvalidoException;
import br.com.autocenterfiap.security.repository.entity.Perfil;
import br.com.autocenterfiap.security.repository.entity.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfisMock;
import static br.com.autocenterfiap.util.UsuarioMockUtil.createUsuarioMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TokenServiceTest {

    private TokenService tokenService;
    private List<Perfil> perfisMock;

    @BeforeEach
    void setUp() {
        perfisMock = createPerfisMock();
        tokenService = new TokenService();
        // Injetando valores simulados
        ReflectionTestUtils.setField(tokenService, "chaveSecreta", "minhaChaveSecreta123");
        ReflectionTestUtils.setField(tokenService, "tempoExpiracao", 5); // 5 minutos
    }

    @Test
    void deveGerarTokenValido() {
        Usuario usuario = createUsuarioMock(1L, "maria", "senha123", perfisMock);

        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);

        DecodedJWT decoded = JWT.decode(token);
        assertEquals("maria", decoded.getSubject());
        assertEquals("Auto Center Fiap", decoded.getIssuer());
    }

    @Test
    void deveGerarRefreshTokenValido() {
        Usuario usuario = createUsuarioMock(1L, "maria", "senha123", perfisMock);

        String refreshToken = tokenService.gerarRefreshToken(usuario);

        assertNotNull(refreshToken);

        DecodedJWT decoded = JWT.decode(refreshToken);
        assertEquals("maria", decoded.getSubject());
        assertEquals("Auto Center Fiap", decoded.getIssuer());
    }

    @Test
    void deveVerificarTokenValido() {
        Usuario usuario = createUsuarioMock(1L, "maria", "senha123", perfisMock);
        String token = tokenService.gerarToken(usuario);

        String subject = tokenService.verificarToken(token);

        assertEquals("maria", subject);
    }

    @Test
    void deveLancarExcecaoParaTokenInvalido() {
        assertThrows(TokenInvalidoException.class, () -> {
            tokenService.verificarToken("token-invalido");
        });
    }

    @Test
    void deveChamarLimparCache() {
        TokenService tokenService = Mockito.mock(TokenService.class);

        tokenService.limparCache("maria");

        verify(tokenService, times(1)).limparCache("maria");
    }
}
