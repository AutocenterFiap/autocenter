package br.com.autocenterfiap.security.exception;


public class UsuarioNaoEncontradoException extends InformacaoNaoEncontradaException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
    public UsuarioNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
