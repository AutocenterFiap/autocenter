package br.com.autocenterfiap.comum;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.produto.domain.exception.EstoqueInsuficienteException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoInativoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handler centralizado para exceções de negócio compartilhadas entre diferentes domínios.
 *
 * Diferente do GlobalExceptionHandler, que trata erros técnicos e inesperados,
 * o NegocioExceptionHandler foca em exceções de negócio que podem ser propagadas
 * entre múltiplos módulos da aplicação, garantindo consistência na resposta.
 *
 * Exemplo de uso:
 * - Ordem de Serviço chama Produto e este lança EstoqueInsuficienteException.
 * - A exceção é capturada aqui e retorna 422 para o cliente.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CompartilhadoExceptionHandler {
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
