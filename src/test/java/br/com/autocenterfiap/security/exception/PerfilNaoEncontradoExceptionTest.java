package br.com.autocenterfiap.security.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerfilNaoEncontradoExceptionTest {

    @Test
    void devePropagarMensagem() {
        PerfilNaoEncontradoException ex =
                new PerfilNaoEncontradoException("Perfil não encontrado");

        assertEquals("Perfil não encontrado", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void devePropagarMensagemECausa() {
        Throwable cause = new RuntimeException("Erro interno");
        PerfilNaoEncontradoException ex =
                new PerfilNaoEncontradoException("Falha ao buscar perfil", cause);

        assertEquals("Falha ao buscar perfil", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void deveSerSubclasseDeInformacaoNaoEncontradaException() {
        PerfilNaoEncontradoException ex =
                new PerfilNaoEncontradoException("Teste");

        assertTrue(ex instanceof InformacaoNaoEncontradaException);
    }
}

