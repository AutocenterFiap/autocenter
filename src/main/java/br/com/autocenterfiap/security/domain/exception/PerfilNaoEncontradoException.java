package br.com.autocenterfiap.security.domain.exception;

import br.com.autocenterfiap.comum.exception.InformacaoNaoEncontradaException;

public class PerfilNaoEncontradoException extends InformacaoNaoEncontradaException {
    public PerfilNaoEncontradoException(String message) {
        super(message);
    }
    public PerfilNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
