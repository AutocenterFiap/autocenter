package br.com.autocenterfiap.cliente.exception;

public class DocumentoInvalidoException extends RuntimeException {

    public DocumentoInvalidoException(String tipoDocumento, String documento) {
        super(String.format("%s inválido: %s. Verifique os dígitos verificadores.", tipoDocumento, documento));
    }
}
