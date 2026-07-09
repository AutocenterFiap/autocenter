package br.com.autocenterfiap.servico.adapter.exception;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.servico.domain.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;
import br.com.autocenterfiap.servico.domain.exception.ServicoEmUsoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.com.autocenterfiap.servico")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServicoExceptionHandler {

    @ExceptionHandler(ServicoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleServicoNaoEncontradoException(
            ServicoNaoEncontradoException ex,
            HttpServletRequest request) {

        ErroResposta erroResposta = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroResposta);
    }

    @ExceptionHandler(ServicoInativoException.class)
    public ResponseEntity<ErroResposta> handleServicoInativoException(
            ServicoInativoException ex,
            HttpServletRequest request) {

        ErroResposta erroResposta = new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Operação Não Permitida",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erroResposta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResposta> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ErroResposta erroResposta = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroResposta);
    }

    @ExceptionHandler(ServicoEmUsoException.class)
    public ResponseEntity<ErroResposta> handleServicoEmUsoException(
            ServicoEmUsoException ex,
            HttpServletRequest request) {

        ErroResposta erroResposta = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erroResposta);
    }
}
