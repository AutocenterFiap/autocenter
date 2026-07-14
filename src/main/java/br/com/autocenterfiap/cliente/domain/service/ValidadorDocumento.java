package br.com.autocenterfiap.cliente.domain.service;

public interface ValidadorDocumento {

    boolean validar(String documento);

    String getTipoDocumento();

    int getTamanhoEsperado();
}

