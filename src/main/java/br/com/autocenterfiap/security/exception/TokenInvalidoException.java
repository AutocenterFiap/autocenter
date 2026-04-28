package br.com.autocenterfiap.security.exception;

import org.springframework.security.access.AccessDeniedException;

public class TokenInvalidoException extends AccessDeniedException {
    public TokenInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
