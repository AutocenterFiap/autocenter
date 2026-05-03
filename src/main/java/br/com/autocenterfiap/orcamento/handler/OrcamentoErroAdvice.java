package br.com.autocenterfiap.orcamento.handler;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.security.exception.InformacaoNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.com.autocenterfiap.orcamento")
@Order(Ordered.HIGHEST_PRECEDENCE)
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