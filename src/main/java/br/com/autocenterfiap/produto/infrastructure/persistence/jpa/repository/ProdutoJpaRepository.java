package br.com.autocenterfiap.produto.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.entity.ProdutoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoJpaRepository extends JpaRepository<ProdutoJpaEntity, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @Query("""
            SELECT p FROM ProdutoJpaEntity p
            WHERE (:categoria IS NULL OR LOWER(p.categoria) = LOWER(:categoria))
              AND (:busca IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                                  OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :busca, '%')))
              AND (:apenasAtivos = false OR p.ativo = true)
            """)
    List<ProdutoJpaEntity> buscarComFiltros(
            @Param("categoria") String categoria,
            @Param("busca") String busca,
            @Param("apenasAtivos") boolean apenasAtivos
        );

    @Query("SELECT p FROM ProdutoJpaEntity p WHERE p.ativo = true AND p.quantidadeEstoque < p.estoqueMinimo")
    List<ProdutoJpaEntity> findProdutosComEstoqueBaixo();

    @Query("SELECT p FROM ProdutoJpaEntity p WHERE p.ativo = true AND p.quantidadeEstoque = 0")
    List<ProdutoJpaEntity> findProdutosSemEstoque();
}
