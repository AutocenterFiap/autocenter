package br.com.autocenterfiap.veiculo.handler;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.veiculo.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.com.autocenterfiap.veiculo")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class VeiculoExceptionHandler {

    @ExceptionHandler({
            RenavamJaCadastradoException.class,
            PlacaJaCadastradaException.class,
            ChassiJaCadastradoException.class
    })
    public ResponseEntity<ErroResposta> handleConflitoDeDados(Exception ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler({
            ChassiInvalidoException.class,
            RenavamInvalidoException.class
    })
    public ResponseEntity<ErroResposta> handleDadoInvalido(Exception ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(VeiculoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleVeiculoNaoEncontrado(VeiculoNaoEncontradoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
}
