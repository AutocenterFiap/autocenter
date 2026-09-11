package br.com.autocenterfiap.handler;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.ordemservico.application.port.ObservabilidadePort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *  GlobalExceptionHandler, trata erros técnicos e inesperados,
 */
@Slf4j
@RestControllerAdvice(basePackages = "br.com.autocenterfiap")
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private final ObservabilidadePort observabilidadePort;

    public GlobalExceptionHandler() {
        this.observabilidadePort = null;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public GlobalExceptionHandler(ObservabilidadePort observabilidadePort) {
        this.observabilidadePort = observabilidadePort;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> handleValidationErrors(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");

        log.warn("Erro de validação na requisição URI={}: {}", request.getRequestURI(), mensagem);
        if (observabilidadePort != null) {
            observabilidadePort.registrarErroIntegracao("api_geral", "VALIDACAO");
        }

        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResposta> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Requisição inválida URI={}: {}", request.getRequestURI(), ex.getMessage());
        if (observabilidadePort != null) {
            observabilidadePort.registrarErroIntegracao("api_geral", "REQUISICAO_INVALIDA");
        }

        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> handleGenericException(Exception ex,
                                                               HttpServletRequest request) {
        log.error("Erro inesperado no servidor (HTTP 500) URI={}: {}", request.getRequestURI(), ex.getMessage(), ex);
        if (observabilidadePort != null) {
            observabilidadePort.registrarErroIntegracao("api_geral", ex.getClass().getSimpleName());
        }

        ErroResposta erro = new ErroResposta(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro inesperado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
