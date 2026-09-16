package br.com.autocenterfiap.produto.adapter.exception;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.produto.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "br.com.autocenterfiap.produto")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProdutoExceptionHandler {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleProdutoNaoEncontrado(
            ProdutoNaoEncontradoException ex, HttpServletRequest request) {

        log.warn("Produto não encontrado na URI={}: {}", request.getRequestURI(), ex.getMessage());
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

        log.warn("Item de produto da OS não encontrado na URI={}: {}", request.getRequestURI(), ex.getMessage());
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

        log.warn("Código de produto já cadastrado na URI={}: {}", request.getRequestURI(), ex.getMessage());
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

        log.warn("Estoque insuficiente na URI={}: {}", request.getRequestURI(), ex.getMessage());
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

        log.warn("Produto inativo na URI={}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Operação Não Permitida",
                ex.getMessage(),
                request.getRequestURI()
        ));
    }
}
