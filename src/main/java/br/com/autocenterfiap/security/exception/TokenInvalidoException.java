package br.com.autocenterfiap.security.exception;

public class TokenInvalidoException extends RegraDeNegocioException{
    public TokenInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
