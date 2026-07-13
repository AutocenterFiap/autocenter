package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.exception.OSItemProdutoNaoEncontradoException;

public class RemoverProdutoNaOrdemServicoUseCase {

    private final OSItemProdutoRepositoryPort itemProdutoRepositoryPort;
    private final ProdutoRepositoryPort produtoRepositoryPort;

    public RemoverProdutoNaOrdemServicoUseCase(OSItemProdutoRepositoryPort itemProdutoRepositoryPort,
                                               ProdutoRepositoryPort produtoRepositoryPort) {
        this.itemProdutoRepositoryPort = itemProdutoRepositoryPort;
        this.produtoRepositoryPort = produtoRepositoryPort;
    }

    public void executar(Long ordemServicoId, Long produtoId) {
        OSItemProduto item = this.itemProdutoRepositoryPort.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
                .orElseThrow(() -> new OSItemProdutoNaoEncontradoException(ordemServicoId, produtoId));

        // Devolve a quantidade ao estoque
        item.getProduto().incrementarEstoque(item.getQuantidade());
        this.produtoRepositoryPort.salvar(item.getProduto());

        this.itemProdutoRepositoryPort.deleteById(item.getId());
    }
}
