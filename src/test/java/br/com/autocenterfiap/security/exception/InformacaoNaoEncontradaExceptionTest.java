package br.com.autocenterfiap.security.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InformacaoNaoEncontradaExceptionTest {

    @Test
    void devePropagarMensagem() {
        InformacaoNaoEncontradaException ex =
                new InformacaoNaoEncontradaException("Informação não encontrada");

        assertEquals("Informação não encontrada", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void devePropagarMensagemECausa() {
        Throwable cause = new RuntimeException("Erro interno");
        InformacaoNaoEncontradaException ex =
                new InformacaoNaoEncontradaException("Falha ao buscar informação", cause);

        assertEquals("Falha ao buscar informação", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void deveSerSubclasseDeRuntimeException() {
        InformacaoNaoEncontradaException ex =
                new InformacaoNaoEncontradaException("Teste");

        assertTrue(ex instanceof RuntimeException);
    }
}

