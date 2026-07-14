package br.com.autocenterfiap.veiculo.adapter.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.autocenterfiap.veiculo.domain.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "br.com.autocenterfiap.veiculo")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class VeiculoExceptionHandler {

    @ExceptionHandler({
        RenavamJaCadastradoException.class,
        PlacaJaCadastradaException.class,
        ChassiJaCadastradoException.class,
        VeiculoEmUsoException.class
    })
    public ResponseEntity<ErrorResponse> handleConflitoDeDados(Exception ex, HttpServletRequest request) {
        ErrorResponse erro = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Conflito de Dados")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler({
        ChassiInvalidoException.class,
        RenavamInvalidoException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleDadoInvalido(Exception ex, HttpServletRequest request) {
        ErrorResponse erro = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erro de Validação")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(VeiculoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleVeiculoNaoEncontrado(VeiculoNaoEncontradoException ex, HttpServletRequest request) {
        ErrorResponse erro = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Recurso Não Encontrado")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
}
