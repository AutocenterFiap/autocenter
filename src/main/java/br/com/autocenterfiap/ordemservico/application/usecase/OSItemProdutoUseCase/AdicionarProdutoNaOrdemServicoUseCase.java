package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoQuantidadeInput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemProdutoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.ProdutoInativoException;
import br.com.autocenterfiap.produto.domain.exception.ProdutoNaoEncontradoException;
import br.com.autocenterfiap.util.Util;

public class AdicionarProdutoNaOrdemServicoUseCase {

    private final OSItemProdutoRepositoryPort itemProdutoRepositoryPort;
    private final ProdutoRepositoryPort produtoRepositoryPort;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public AdicionarProdutoNaOrdemServicoUseCase(OSItemProdutoRepositoryPort itemProdutoRepositoryPort, ProdutoRepositoryPort produtoRepositoryPort, OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        this.itemProdutoRepositoryPort = itemProdutoRepositoryPort;
        this.produtoRepositoryPort = produtoRepositoryPort;
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
    }

    public OSItemProdutoOutput executar(Long ordemServicoId, OSItemProdutoQuantidadeInput itemProdutoInput) {
        Produto produto = this.produtoRepositoryPort.buscarPorId(itemProdutoInput.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(itemProdutoInput.produtoId()));

        if (!produto.getAtivo()) {
            throw new ProdutoInativoException(produto.getCodigo());
        }

        // Decrementa o estoque imediatamente (reserva o produto)
        produto.decrementarEstoque(itemProdutoInput.quantidade());

        OrdemServico ordemServico = this.ordemServicoRepositoryPort.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada para o id: "
                        + ordemServicoId));

        OSItemProduto item = new OSItemProduto();
        item.setOrdemServico(ordemServico);
        item.setProduto(produto);
        item.setQuantidade(itemProdutoInput.quantidade());
        item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());

        // Recalcula o valor da OS após adicionar o produto
        ordemServico.getOsItensProdutos().add(item);
        ordemServico.setValorTotal(Util.calcularValorTotal(ordemServico));

        OSItemProduto produtoSalvo = this.itemProdutoRepositoryPort.save(item);

        return OSItemProdutoApplicationMapper.toOutput(produtoSalvo);
    }
}
