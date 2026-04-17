package br.com.autocenterfiap.veiculo.handler;

import br.com.autocenterfiap.cliente.model.ErroResposta;
import br.com.autocenterfiap.veiculo.exception.ChassiJaCadastradoException;
import br.com.autocenterfiap.veiculo.exception.PlacaJaCadastradaException;
import br.com.autocenterfiap.veiculo.exception.RenavamJaCadastradoException;
import br.com.autocenterfiap.veiculo.exception.VeiculoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(0)
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
