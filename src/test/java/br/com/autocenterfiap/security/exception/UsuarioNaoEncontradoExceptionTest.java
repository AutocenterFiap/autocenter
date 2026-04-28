package br.com.autocenterfiap.security.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioNaoEncontradoExceptionTest {

    @Test
    void devePropagarMensagem() {
        UsuarioNaoEncontradoException ex =
                new UsuarioNaoEncontradoException("Usuário não encontrado");

        assertEquals("Usuário não encontrado", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void devePropagarMensagemECausa() {
        Throwable cause = new RuntimeException("Erro interno");
        UsuarioNaoEncontradoException ex =
                new UsuarioNaoEncontradoException("Falha ao buscar usuário", cause);

        assertEquals("Falha ao buscar usuário", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void deveSerSubclasseDeInformacaoNaoEncontradaException() {
        UsuarioNaoEncontradoException ex =
                new UsuarioNaoEncontradoException("Teste");

        assertTrue(ex instanceof InformacaoNaoEncontradaException);
    }
}

