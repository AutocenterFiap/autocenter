package br.com.autocenterfiap.comum;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.produto.domain.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoInativoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handler centralizado para exceções de negócio compartilhadas entre diferentes domínios.
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CompartilhadoExceptionHandler {
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
