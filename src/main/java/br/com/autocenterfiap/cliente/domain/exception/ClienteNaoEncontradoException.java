package br.com.autocenterfiap.cliente.domain.exception;

public class ClienteNaoEncontradoException extends DomainException {

    public ClienteNaoEncontradoException(Long id) {
        super(String.format("Cliente com ID %d não foi encontrado", id));
    }

    public ClienteNaoEncontradoException(String identifier) {
        super(String.format("Cliente com identificador %s não foi encontrado", identifier));
    }
}

