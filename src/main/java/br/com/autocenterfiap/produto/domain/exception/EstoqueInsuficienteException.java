package br.com.autocenterfiap.produto.domain.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String codigoProduto, Integer disponivel, Integer solicitado) {
        super(String.format(
                "Estoque insuficiente para o produto '%s'. Disponível: %d, Solicitado: %d",
                codigoProduto, disponivel, solicitado
        ));
    }
}
