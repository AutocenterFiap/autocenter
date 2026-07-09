package br.com.autocenterfiap.produto.application.usecase;

import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import br.com.autocenterfiap.produto.application.mapper.ProdutoApplicationMapper;
import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;

import java.util.List;

public class ListarProdutosUseCase {

    private final ProdutoRepositoryPort repositoryPort;

    public ListarProdutosUseCase(ProdutoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public List<ProdutoOutput> executar(String categoria, String busca) {
        return repositoryPort.buscarComFiltros(categoria, busca, true)
                .stream()
                .map(ProdutoApplicationMapper::toOutput)
                .toList();
    }
}
