package br.com.autocenterfiap.cliente.exception;

public class DocumentoJaCadastradoException extends InformacaoJaCadastradaException {

    public DocumentoJaCadastradoException(String documento) {
        super("Já existe um cliente cadastrado com o documento: " + documento);
    }
}
