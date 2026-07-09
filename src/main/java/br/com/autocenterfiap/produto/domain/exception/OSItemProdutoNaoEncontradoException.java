package br.com.autocenterfiap.produto.domain.exception;

public class OSItemProdutoNaoEncontradoException extends RuntimeException {

    public OSItemProdutoNaoEncontradoException(Long ordemServicoId, Long produtoId) {
        super(String.format("Produto ID %d não encontrado na Ordem de Serviço ID %d", produtoId, ordemServicoId));
    }
}
