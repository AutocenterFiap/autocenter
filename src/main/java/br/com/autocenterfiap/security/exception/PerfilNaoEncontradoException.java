package br.com.autocenterfiap.security.exception;


public class PerfilNaoEncontradoException extends InformacaoNaoEncontradaException {
    public PerfilNaoEncontradoException(String message) {
        super(message);
    }
    public PerfilNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
