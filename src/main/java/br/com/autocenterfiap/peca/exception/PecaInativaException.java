package br.com.autocenterfiap.peca.exception;

public class PecaInativaException extends RuntimeException {

    public PecaInativaException(String codigoPeca) {
        super("A peça '" + codigoPeca + "' está inativa e não pode ser utilizada em uma Ordem de Serviço.");
    }
}