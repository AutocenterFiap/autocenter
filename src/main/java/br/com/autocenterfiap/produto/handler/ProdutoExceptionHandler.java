package br.com.autocenterfiap.produto.handler;

import br.com.autocenterfiap.cliente.model.ErroResposta;
import br.com.autocenterfiap.produto.exception.CodigoJaCadastradoException;
import br.com.autocenterfiap.produto.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.produto.exception.OSItemProdutoNaoEncontradoException;
import br.com.autocenterfiap.produto.exception.ProdutoInativoException;
import br.com.autocenterfiap.produto.exception.ProdutoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.com.autocenterfiap.produto")
public class ProdutoExceptionHandler {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleProdutoNaoEncontrado(
            ProdutoNaoEncontradoException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(OSItemProdutoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleOSItemProdutoNaoEncontrado(
            OSItemProdutoNaoEncontradoException ex, HttpServletRequest request) {

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

    @ExceptionHandler(ProdutoInativoException.class)
    public ResponseEntity<ErroResposta> handleProdutoInativo(
            ProdutoInativoException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Operação Não Permitida",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }
}
