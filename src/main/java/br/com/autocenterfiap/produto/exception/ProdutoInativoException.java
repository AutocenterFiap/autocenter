package br.com.autocenterfiap.produto.exception;

public class ProdutoInativoException extends RuntimeException {

    public ProdutoInativoException(String codigoProduto) {
        super("O produto '" + codigoProduto + "' está inativo e não pode ser utilizado em uma Ordem de Serviço.");
    }
}
