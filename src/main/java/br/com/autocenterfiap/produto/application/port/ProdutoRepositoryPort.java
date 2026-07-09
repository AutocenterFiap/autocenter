package br.com.autocenterfiap.produto.application.port;

import br.com.autocenterfiap.produto.domain.entity.Produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepositoryPort {

    List<Produto> buscarComFiltros(String categoria, String busca, boolean apenasAtivos);

    Optional<Produto> buscarPorId(Long id);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    Produto salvar(Produto produto);

    List<Produto> findProdutosComEstoqueBaixo();

    List<Produto> findProdutosSemEstoque();
}
