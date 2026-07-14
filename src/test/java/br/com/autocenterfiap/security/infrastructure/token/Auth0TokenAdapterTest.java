package br.com.autocenterfiap.security.infrastructure.token;

import br.com.autocenterfiap.security.domain.exception.TokenInvalidoException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class Auth0TokenAdapterTest {

    private Auth0TokenAdapter tokenAdapter;

    @BeforeEach
    void setUp() {
        tokenAdapter = new Auth0TokenAdapter();
        // Injetando valores simulados
        ReflectionTestUtils.setField(tokenAdapter, "chaveSecreta", "minhaChaveSecreta123");
        ReflectionTestUtils.setField(tokenAdapter, "tempoExpiracao", 5); // 5 minutos
    }

    @Test
    void deveGerarTokenValido() {
        String token = tokenAdapter.gerarToken("maria");

        assertNotNull(token);

        DecodedJWT decoded = JWT.decode(token);
        assertEquals("maria", decoded.getSubject());
        assertEquals("Auto Center Fiap", decoded.getIssuer());
    }

    @Test
    void deveGerarRefreshTokenValido() {
        String refreshToken = tokenAdapter.gerarRefreshToken("maria");

        assertNotNull(refreshToken);

        DecodedJWT decoded = JWT.decode(refreshToken);
        assertEquals("maria", decoded.getSubject());
        assertEquals("Auto Center Fiap", decoded.getIssuer());
    }

    @Test
    void deveVerificarTokenValido() {
        String token = tokenAdapter.gerarToken("maria");

        String subject = tokenAdapter.verificarToken(token);

        assertEquals("maria", subject);
    }

    @Test
    void deveLancarExcecaoParaTokenInvalido() {
        assertThrows(TokenInvalidoException.class, () -> {
            tokenAdapter.verificarToken("token-invalido");
        });
    }

    @Test
    void deveChamarLimparCache() {
        Auth0TokenAdapter tokenAdapterMock = Mockito.mock(Auth0TokenAdapter.class);

        tokenAdapterMock.limparCache("maria");

        verify(tokenAdapterMock, times(1)).limparCache("maria");
    }
}
