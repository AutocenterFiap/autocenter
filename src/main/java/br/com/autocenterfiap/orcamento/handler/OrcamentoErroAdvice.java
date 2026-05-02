package br.com.autocenterfiap.orcamento.handler;

import br.com.autocenterfiap.security.exception.InformacaoNaoEncontradaException;
import br.com.autocenterfiap.security.handler.ErroResposta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "br.com.autocenterfiap.orcamento")
public class OrcamentoErroAdvice {
    @ExceptionHandler(InformacaoNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> tratarErroRegraDeNegocio(InformacaoNaoEncontradaException ex,
                                                                 HttpServletRequest request) {
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

}