package br.com.autocenterfiap.produto.infrastructure.persistence.jpa.adapter;

import br.com.autocenterfiap.produto.application.port.ProdutoRepositoryPort;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.entity.ProdutoJpaEntity;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.mapper.ProdutoJpaMapper;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.repository.ProdutoJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ProdutoRepositoryJpaAdapter implements ProdutoRepositoryPort {

    private final ProdutoJpaRepository produtoJpaRepository;

    @Override
    public List<Produto> buscarComFiltros(String categoria, String busca, boolean apenasAtivos) {
        return produtoJpaRepository.buscarComFiltros(categoria, busca, apenasAtivos)
                .stream()
                .map(ProdutoJpaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return produtoJpaRepository.findById(id)
                .map(ProdutoJpaMapper::toDomain);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return produtoJpaRepository.existsByCodigo(codigo);
    }

    @Override
    public boolean existsByCodigoAndIdNot(String codigo, Long id) {
        return produtoJpaRepository.existsByCodigoAndIdNot(codigo, id);
    }

    @Override
    public Produto salvar(Produto produto) {
        ProdutoJpaEntity entity = ProdutoJpaMapper.toJpaEntity(produto);
        ProdutoJpaEntity savedEntity = produtoJpaRepository.save(entity);
        return ProdutoJpaMapper.toDomain(savedEntity);
    }

    @Override
    public List<Produto> findProdutosComEstoqueBaixo() {
        return produtoJpaRepository.findProdutosComEstoqueBaixo()
                .stream()
                .map(ProdutoJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<Produto> findProdutosSemEstoque() {
        return produtoJpaRepository.findProdutosSemEstoque()
                .stream()
                .map(ProdutoJpaMapper::toDomain)
                .toList();
    }
}
