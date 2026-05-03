package br.com.autocenterfiap.cliente.handler;

import br.com.autocenterfiap.cliente.exception.ClienteEmUsoException;
import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoInvalidoException;
import br.com.autocenterfiap.cliente.exception.DocumentoNaoPodeSerAlteradoException;
import br.com.autocenterfiap.cliente.exception.InformacaoJaCadastradaException;
import br.com.autocenterfiap.comum.model.ErroResposta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.com.autocenterfiap.cliente")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClienteExceptionHandler {

    @ExceptionHandler(InformacaoJaCadastradaException.class)
    public ResponseEntity<ErroResposta> handleInformacaoJaCadastrada(
            InformacaoJaCadastradaException ex,
            HttpServletRequest request) {

        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ClienteEmUsoException.class)
    public ResponseEntity<ErroResposta> handleClienteEmUso(
            ClienteEmUsoException ex,
            HttpServletRequest request) {

        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleClienteNaoEncontrado(
            ClienteNaoEncontradoException ex,
            HttpServletRequest request) {

        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(DocumentoInvalidoException.class)
    public ResponseEntity<ErroResposta> handleDocumentoInvalido(
            DocumentoInvalidoException ex,
            HttpServletRequest request) {

        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Documento Inválido",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(DocumentoNaoPodeSerAlteradoException.class)
    public ResponseEntity<ErroResposta> handleDocumentoNaoPodeSerAlterado(
            DocumentoNaoPodeSerAlteradoException ex,
            HttpServletRequest request) {

        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Operação Não Permitida",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}
