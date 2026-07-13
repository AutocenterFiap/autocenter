package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoQuantidadeInput;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemProdutoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.OSItemProdutoNaoEncontradoException;
import br.com.autocenterfiap.util.Util;

public class AtualizarQuantidadeUseCase {

    private final OSItemProdutoRepositoryPort itemProdutoRepositoryPort;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;
    private final ProdutoRepositoryPort produtoRepositoryPort;

    public AtualizarQuantidadeUseCase(OSItemProdutoRepositoryPort itemProdutoRepositoryPort,
                                      OrdemServicoRepositoryPort ordemServicoRepositoryPort,
                                      ProdutoRepositoryPort produtoRepositoryPort)
    {
        this.itemProdutoRepositoryPort = itemProdutoRepositoryPort;
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
        this.produtoRepositoryPort = produtoRepositoryPort;
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

        this.produtoRepositoryPort.salvar(produto);

        item.setQuantidade(quantidadeInput.quantidade());

        // Recalcula o valor total da Ordem de Serviço após atualizar a quantidade
        OrdemServico ordemServico = this.ordemServicoRepositoryPort.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada para o id: " + ordemServicoId));
        ordemServico.setValorTotal(Util.calcularValorTotal(ordemServico));
        this.ordemServicoRepositoryPort.save(ordemServico);

        return OSItemProdutoApplicationMapper.toOutput(this.itemProdutoRepositoryPort.save(item));
    }
}
