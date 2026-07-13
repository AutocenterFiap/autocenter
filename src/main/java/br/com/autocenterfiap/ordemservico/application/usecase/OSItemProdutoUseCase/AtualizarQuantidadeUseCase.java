package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoQuantidadeInput;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemProdutoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.OSItemProdutoNaoEncontradoException;
import br.com.autocenterfiap.util.Util;

public class AtualizarQuantidadeUseCase {

    private final OSItemProdutoRepositoryPort itemProdutoRepositoryPort;

    public AtualizarQuantidadeUseCase(OSItemProdutoRepositoryPort itemProdutoRepositoryPort)
    {
        this.itemProdutoRepositoryPort = itemProdutoRepositoryPort;
    }

    public OSItemProdutoOutput executar(Long ordemServicoId, Long produtoId, OSItemProdutoQuantidadeInput quantidadeInput) {
        OSItemProduto item = this.itemProdutoRepositoryPort.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
                .orElseThrow(() -> new OSItemProdutoNaoEncontradoException(ordemServicoId, produtoId));

        Produto produto = item.getProduto();
        int diferencaQuantidade = quantidadeInput.quantidade() - item.getQuantidade();

        if (diferencaQuantidade > 0) {
            // Aumentou a quantidade: reservar mais estoque
            produto.decrementarEstoque(diferencaQuantidade);
        } else if (diferencaQuantidade < 0) {
            // Reduziu a quantidade: devolver ao estoque
            produto.incrementarEstoque(Math.abs(diferencaQuantidade));
        }

        item.setQuantidade(quantidadeInput.quantidade());

        // Recalcula o valor total da Ordem de Serviço após atualizar a quantidade
        OrdemServico ordemServico = item.getOrdemServico();
        ordemServico.setValorTotal(Util.calcularValorTotal(ordemServico));

        return OSItemProdutoApplicationMapper.toOutput(this.itemProdutoRepositoryPort.save(item));
    }
}
