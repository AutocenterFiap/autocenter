package br.com.autocenterfiap.peca.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String codigoPeca, Integer disponivel, Integer solicitado) {
        super(String.format(
                "Estoque insuficiente para a peça '%s'. Disponível: %d, Solicitado: %d",
                codigoPeca, disponivel, solicitado
        ));
    }
}