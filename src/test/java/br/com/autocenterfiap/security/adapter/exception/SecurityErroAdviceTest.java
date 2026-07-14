package br.com.autocenterfiap.security.adapter.exception;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.handler.GlobalExceptionHandler;
import br.com.autocenterfiap.comum.exception.InformacaoNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityErroAdviceTest {
    private GlobalExceptionHandler globalExceptionHandler;
    private SecurityErroAdvice tratamentoErro;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        tratamentoErro = new SecurityErroAdvice();
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/teste");
    }

    @Test
    void deveTratarErro400ComMensagemDePerfilInvalido() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Erro: PerfilType inválido");

        ResponseEntity<ErroResposta> resposta = tratamentoErro.tratarErro400(ex, request);

        assertEquals(400, resposta.getStatusCodeValue());
        assertTrue(resposta.getBody().getMensagem().contains("perfil informado é inválido"));
    }

    @Test
    void deveTratarErro400ComMensagemGenerica() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Erro genérico");

        ResponseEntity<ErroResposta> resposta = tratamentoErro.tratarErro400(ex, request);

        assertEquals(400, resposta.getStatusCodeValue());
        assertTrue(resposta.getBody().getMensagem().contains("Verifique a sintaxe"));
    }

    @Test
    void deveTratarErro403() {
        AccessDeniedException ex = new AccessDeniedException("Acesso negado");

        ResponseEntity<String> resposta = tratamentoErro.tratarErro403(ex);

        assertEquals(403, resposta.getStatusCodeValue());
        assertEquals("Acesso negado", resposta.getBody());
    }

    @Test
    void deveTratarErro401() {
        AuthenticationException ex = mock(AuthenticationException.class);
        when(ex.getMessage()).thenReturn("Não autenticado");

        ResponseEntity<String> resposta = tratamentoErro.tratarErro401(ex);

        assertEquals(401, resposta.getStatusCodeValue());
        assertEquals("Não autenticado", resposta.getBody());
    }

    @Test
    void deveTratarErro404() {
        InformacaoNaoEncontradaException ex =
                new InformacaoNaoEncontradaException("Usuário não encontrado");

        ResponseEntity<ErroResposta> resposta = tratamentoErro.tratarErroRegraDeNegocio(ex, request);

        assertEquals(404, resposta.getStatusCodeValue());
        assertEquals("Usuário não encontrado", resposta.getBody().getMensagem());
    }

    @Test
    void deveTratarErro500() {
        Exception ex = new Exception("Falha inesperada");

        ResponseEntity<ErroResposta> resposta = globalExceptionHandler.handleGenericException(ex, request);

        assertEquals(500, resposta.getStatusCodeValue());
        assertTrue(resposta.getBody().getErro().contains("Erro inesperado"));
    }

    @Test
    void deveTratarErroValidacao400() {
        // cria um objeto alvo fictício
        Object target = new Object();
        BindingResult bindingResult = new BeanPropertyBindingResult(target, "obj");

        // adiciona um erro de campo
        bindingResult.addError(new FieldError("obj", "campo", "mensagem inválida"));

        // cria a exceção com o BindingResult
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> resposta = tratamentoErro.tratarErro400(ex);

        assertEquals(400, resposta.getStatusCodeValue());
        List<?> erros = (List<?>) resposta.getBody();
        assertFalse(erros.isEmpty());
    }
}

