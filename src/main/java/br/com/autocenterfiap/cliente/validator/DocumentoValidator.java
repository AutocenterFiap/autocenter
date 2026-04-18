package br.com.autocenterfiap.cliente.validator;

public interface DocumentoValidator {

    boolean isValid(String documento);

    String getTipoDocumento();

    int getTamanhoEsperado();
}
