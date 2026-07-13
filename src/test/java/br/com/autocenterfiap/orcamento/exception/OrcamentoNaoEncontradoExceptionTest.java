package br.com.autocenterfiap.orcamento.exception;

import br.com.autocenterfiap.orcamento.application.exception.OrcamentoNaoEncontradoException;
import br.com.autocenterfiap.security.exception.InformacaoNaoEncontradaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrcamentoNaoEncontradoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com ID e mensagem correta")
    void deveCriarExcecaoComId() {
        Long id = 42L;
        OrcamentoNaoEncontradoException ex = new OrcamentoNaoEncontradoException(id);

        assertEquals("Orcamento não encontrado com o ID: 42", ex.getMessage());
        assertTrue(ex instanceof InformacaoNaoEncontradaException);
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem customizada")
    void deveCriarExcecaoComMensagemCustomizada() {
        String mensagem = "Orçamento não localizado no sistema";
        OrcamentoNaoEncontradoException ex = new OrcamentoNaoEncontradoException(mensagem);

        assertEquals(mensagem, ex.getMessage());
        assertTrue(ex instanceof InformacaoNaoEncontradaException);
    }
}
