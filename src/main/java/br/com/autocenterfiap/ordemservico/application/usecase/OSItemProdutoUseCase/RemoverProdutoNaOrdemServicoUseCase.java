package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.produto.domain.exception.OSItemProdutoNaoEncontradoException;
import br.com.autocenterfiap.util.Util;
import org.springframework.transaction.annotation.Transactional;

public class RemoverProdutoNaOrdemServicoUseCase {

    private final OSItemProdutoRepositoryPort itemProdutoRepositoryPort;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public RemoverProdutoNaOrdemServicoUseCase(OSItemProdutoRepositoryPort itemProdutoRepositoryPort,
                                               OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        this.itemProdutoRepositoryPort = itemProdutoRepositoryPort;
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
    }

    @Transactional
    public void executar(Long ordemServicoId, Long produtoId) {
        OSItemProduto item = this.itemProdutoRepositoryPort.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
                .orElseThrow(() -> new OSItemProdutoNaoEncontradoException(ordemServicoId, produtoId));

        // Devolve a quantidade ao estoque
        item.getProduto().incrementarEstoque(item.getQuantidade());

        OrdemServico os = item.getOrdemServico();
        os.getOsItensProdutos().remove(item);

        // Recalcula o valor total da OS e persiste
        os.setValorTotal(Util.calcularValorTotal(os));
        this.ordemServicoRepositoryPort.save(os);
    }
}
