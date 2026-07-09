package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.MovimentacaoEstoqueInput;
import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import br.com.autocenterfiap.produto.application.mapper.ProdutoApplicationMapper;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.ProdutoNaoEncontradoException;

public class AdicionarEstoqueUseCase {

    private final ProdutoRepositoryPort repositoryPort;

    public AdicionarEstoqueUseCase(ProdutoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public ProdutoOutput executar(Long id, MovimentacaoEstoqueInput input) {
        Produto produto = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        produto.incrementarEstoque(input.getQuantidade());
        Produto produtoSalvo = repositoryPort.salvar(produto);
        return ProdutoApplicationMapper.toOutput(produtoSalvo);
    }
}
