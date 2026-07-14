package br.com.autocenterfiap.cliente.domain.exception;

public class ClienteEmailJaCadastradoException extends DomainException {

    public ClienteEmailJaCadastradoException(String email) {
        super(String.format("Cliente com email %s já foi cadastrado", email));
    }
}

