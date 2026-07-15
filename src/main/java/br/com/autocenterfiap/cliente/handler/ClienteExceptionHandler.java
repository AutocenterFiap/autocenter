package br.com.autocenterfiap.cliente.handler;

import br.com.autocenterfiap.cliente.domain.exception.ClienteDocumentoInvalidoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteDocumentoJaCadastradoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteDocumentoNaoPodeSerAlteradoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteEmUsoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteEmailJaCadastradoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.comum.model.ErroResposta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClienteExceptionHandler {

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleClienteNaoEncontrado(
            ClienteNaoEncontradoException ex,
            HttpServletRequest request) {
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Cliente não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(ClienteDocumentoJaCadastradoException.class)
    public ResponseEntity<ErroResposta> handleDocumentoJaCadastrado(
            ClienteDocumentoJaCadastradoException ex,
            HttpServletRequest request) {
        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Documento já cadastrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ClienteEmailJaCadastradoException.class)
    public ResponseEntity<ErroResposta> handleEmailJaCadastrado(
            ClienteEmailJaCadastradoException ex,
            HttpServletRequest request) {
        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Email já cadastrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ClienteDocumentoInvalidoException.class)
    public ResponseEntity<ErroResposta> handleDocumentoInvalido(
            ClienteDocumentoInvalidoException ex,
            HttpServletRequest request) {
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Documento inválido",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(ClienteDocumentoNaoPodeSerAlteradoException.class)
    public ResponseEntity<ErroResposta> handleDocumentoNaoPodeSerAlterado(
            ClienteDocumentoNaoPodeSerAlteradoException ex,
            HttpServletRequest request) {
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Alteração de documento não permitida",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(ClienteEmUsoException.class)
    public ResponseEntity<ErroResposta> handleClienteEmUso(
            ClienteEmUsoException ex,
            HttpServletRequest request) {
        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Cliente em uso",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }
}

