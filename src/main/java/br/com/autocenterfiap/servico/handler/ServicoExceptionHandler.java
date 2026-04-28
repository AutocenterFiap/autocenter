package br.com.autocenterfiap.servico.handler;

import br.com.autocenterfiap.servico.exception.ServicoNaoEncontradoException;
import br.com.autocenterfiap.servico.model.ErroResposta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServicoExceptionHandler {

    @ExceptionHandler(ServicoNaoEncontradoException.class)
    ResponseEntity<ErroResposta> handleServicoNaoEncontradoException(
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

}
