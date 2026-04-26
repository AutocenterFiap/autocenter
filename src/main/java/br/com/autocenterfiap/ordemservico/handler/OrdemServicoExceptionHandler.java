package br.com.autocenterfiap.ordemservico.handler;

import br.com.autocenterfiap.cliente.model.ErroResposta;
import br.com.autocenterfiap.ordemservico.exception.OrdemServicoJaAbertaParaVeiculoException;
import br.com.autocenterfiap.ordemservico.exception.OrdemServicoNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class OrdemServicoExceptionHandler {

    @ExceptionHandler(OrdemServicoJaAbertaParaVeiculoException.class)
    public ResponseEntity<ErroResposta> handleConflitoDeDados(OrdemServicoJaAbertaParaVeiculoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(OrdemServicoNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> handleOrdemServicoNaoEncontrada(OrdemServicoNaoEncontradaException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
}
