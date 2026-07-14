package br.com.autocenterfiap.security.domain.exception;

import com.auth0.jwt.exceptions.JWTCreationException;

public class FalhaCriacaoTokenException extends JWTCreationException {
    public FalhaCriacaoTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
