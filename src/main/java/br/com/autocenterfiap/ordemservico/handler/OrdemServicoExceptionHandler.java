package br.com.autocenterfiap.ordemservico.handler;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.ordemservico.exception.*;
import br.com.autocenterfiap.servico.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.exception.ServicoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.com.autocenterfiap.ordemservico")
@Order(Ordered.HIGHEST_PRECEDENCE)
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

    @ExceptionHandler(StatusOSInvalidoException.class)
    public ResponseEntity<ErroResposta> handleValidacaoDeDados(StatusOSInvalidoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(StatusOSItemInvalidoException.class)
    public ResponseEntity<ErroResposta> handleStatusOSItemInvalido(StatusOSItemInvalidoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(OSItemServicoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleOSItemServicoNaoEncontrado(OSItemServicoNaoEncontradoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(ServicoInativoException.class)
    public ResponseEntity<ErroResposta> handleServicoInativo(ServicoInativoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Serviço Inativo",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(ServicoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleServicoNaoEncontrado(ServicoNaoEncontradoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
}
