package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.domain.exception.ProdutoNaoEncontradoException;

public class DesativarProdutoUseCase {

    private final ProdutoRepositoryPort repositoryPort;

    public DesativarProdutoUseCase(ProdutoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public void executar(Long id) {
        Produto produto = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        produto.desativar();
        repositoryPort.salvar(produto);
    }
}
