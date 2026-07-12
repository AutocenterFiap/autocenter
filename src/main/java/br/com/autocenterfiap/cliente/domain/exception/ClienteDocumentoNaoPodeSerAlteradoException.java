package br.com.autocenterfiap.cliente.domain.exception;

public class ClienteDocumentoNaoPodeSerAlteradoException extends DomainException {

    public ClienteDocumentoNaoPodeSerAlteradoException() {
        super("O documento do cliente não pode ser alterado após o cadastro");
    }
}

