package br.com.autocenterfiap.cliente.exception;

public class ClienteEmUsoException extends RuntimeException {
    public ClienteEmUsoException(String message) {
        super(message);
    }
}
