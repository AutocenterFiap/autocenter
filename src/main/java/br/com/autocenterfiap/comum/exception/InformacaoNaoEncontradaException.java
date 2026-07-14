package br.com.autocenterfiap.comum.exception;

public class InformacaoNaoEncontradaException extends RuntimeException {

    public InformacaoNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
    public InformacaoNaoEncontradaException(String message) {
        super(message);
    }

}
