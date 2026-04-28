package br.com.autocenterfiap.produto.repository;

import br.com.autocenterfiap.produto.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @Query("""
            SELECT p FROM Produto p
            WHERE (:categoria IS NULL OR LOWER(p.categoria) = LOWER(:categoria))
              AND (:busca IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                                  OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :busca, '%')))
              AND (:apenasAtivos = false OR p.ativo = true)
            """)
    List<Produto> buscarComFiltros(
            @Param("categoria") String categoria,
            @Param("busca") String busca,
            @Param("apenasAtivos") boolean apenasAtivos
    );

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.quantidadeEstoque < p.estoqueMinimo")
    List<Produto> findProdutosComEstoqueBaixo();

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.quantidadeEstoque = 0")
    List<Produto> findProdutosSemEstoque();
}
