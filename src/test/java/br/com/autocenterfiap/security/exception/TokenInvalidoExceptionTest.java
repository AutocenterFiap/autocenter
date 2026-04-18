package br.com.autocenterfiap.security.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenInvalidoExceptionTest {

    @Test
    void devePropagarMensagemECausa() {
        Throwable cause = new RuntimeException("Token corrompido");
        TokenInvalidoException ex =
                new TokenInvalidoException("Token inválido", cause);

        assertEquals("Token inválido", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void deveSerSubclasseDeAccessDeniedException() {
        TokenInvalidoException ex =
                new TokenInvalidoException("Teste", new RuntimeException());

        assertTrue(ex instanceof org.springframework.security.access.AccessDeniedException);
    }
}

