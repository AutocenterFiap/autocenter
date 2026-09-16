package br.com.autocenterfiap.security.adapter.exception;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.comum.exception.InformacaoNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "br.com.autocenterfiap.security")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityErroAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> tratarErro400(MethodArgumentNotValidException ex) {
        log.warn("Erro de validação security nos campos enviados");
        var erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarErro400(HttpMessageNotReadableException ex,
                                                      HttpServletRequest request) {
            log.warn("Erro na leitura do JSON security na URI={}: {}", request.getRequestURI(), ex.getMessage());
            Map<String, String> error = new HashMap<>();

            String message = null;

            if (ex.getMessage().contains("PerfilType")) {
                message = "O perfil informado é inválido. Valores aceitos: [READ, ADMIN, WRITE]";
            } else {
                message = "Verifique a sintaxe dos campos.";
            }

            ErroResposta erro = new ErroResposta(
                    HttpStatus.BAD_REQUEST.value(),
                    "Erro na leitura do JSON",
                    message,
                    request.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> tratarErro403(AccessDeniedException ex) {
        log.warn("Acesso negado (403): {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> tratarErro401(AuthenticationException ex) {
        log.warn("Falha de autenticação (401): {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(InformacaoNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> tratarErroRegraDeNegocio(InformacaoNaoEncontradaException ex,
                                                                 HttpServletRequest request) {
        log.warn("Informação de segurança não encontrada na URI={}: {}", request.getRequestURI(), ex.getMessage());
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

}