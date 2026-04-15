package br.com.autocenterfiap.cliente.exception;

public class DocumentoNaoPodeSerAlteradoException extends RuntimeException {

    public DocumentoNaoPodeSerAlteradoException() {
        super("O documento (CPF/CNPJ) não pode ser alterado após o cadastro do cliente.");
    }
}
