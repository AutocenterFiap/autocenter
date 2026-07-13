package br.com.autocenterfiap.orcamento.adapter.exception;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.security.exception.InformacaoNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class OrcamentoErroAdviceTest {

    private OrcamentoErroAdvice advice;

    @BeforeEach
    void setUp() {
        advice = new OrcamentoErroAdvice();
    }

    @Test
    void deveTratarInformacaoNaoEncontradaException() {
        String mensagemErro = "Informação não encontrada";
        InformacaoNaoEncontradaException exception =
                new InformacaoNaoEncontradaException(mensagemErro);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/orcamentos/1");

        ResponseEntity<ErroResposta> resposta =
                advice.tratarErroRegraDeNegocio(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), resposta.getBody().getStatus());
        assertEquals("Recurso Não Encontrado", resposta.getBody().getErro());
        assertEquals(mensagemErro, resposta.getBody().getMensagem());
        assertEquals("/orcamentos/1", resposta.getBody().getCaminho());
    }
}
