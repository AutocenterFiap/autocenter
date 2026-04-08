package br.com.autocenterfiap.security.exception;

public class FalhaCriacaoTokenException extends RegraDeNegocioException{
    public FalhaCriacaoTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
