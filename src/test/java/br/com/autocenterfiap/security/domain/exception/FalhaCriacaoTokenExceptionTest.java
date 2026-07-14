package br.com.autocenterfiap.security.domain.exception;

import com.auth0.jwt.exceptions.JWTCreationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FalhaCriacaoTokenExceptionTest {

    @Test
    void devePropagarMensagemECausa() {
        Throwable cause = new RuntimeException("Erro interno");
        FalhaCriacaoTokenException ex =
                new FalhaCriacaoTokenException("Falha ao criar token", cause);

        assertEquals("Falha ao criar token", ex.getMessage());

        assertEquals(cause, ex.getCause());

        assertTrue(ex instanceof JWTCreationException);
    }
}

