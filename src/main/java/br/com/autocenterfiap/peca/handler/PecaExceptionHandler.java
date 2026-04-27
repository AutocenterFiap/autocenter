package br.com.autocenterfiap.peca.handler;

import br.com.autocenterfiap.cliente.model.ErroResposta;
import br.com.autocenterfiap.peca.exception.CodigoJaCadastradoException;
import br.com.autocenterfiap.peca.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.peca.exception.OSItemPecaNaoEncontradoException;
import br.com.autocenterfiap.peca.exception.PecaInativaException;
import br.com.autocenterfiap.peca.exception.PecaNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(0)
public class PecaExceptionHandler {

    @ExceptionHandler(PecaNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> handlePecaNaoEncontrada(
            PecaNaoEncontradaException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(OSItemPecaNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleOSItemNaoEncontrado(
            OSItemPecaNaoEncontradoException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(CodigoJaCadastradoException.class)
    public ResponseEntity<ErroResposta> handleCodigoJaCadastrado(
            CodigoJaCadastradoException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErroResposta> handleEstoqueInsuficiente(
            EstoqueInsuficienteException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Estoque Insuficiente",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(PecaInativaException.class)
    public ResponseEntity<ErroResposta> handlePecaInativa(
            PecaInativaException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Operação Não Permitida",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }
}
