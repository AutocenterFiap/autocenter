package br.com.autocenterfiap.security.domain.exception;

import br.com.autocenterfiap.comum.exception.InformacaoNaoEncontradaException;

public class UsuarioNaoEncontradoException extends InformacaoNaoEncontradaException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
    public UsuarioNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
