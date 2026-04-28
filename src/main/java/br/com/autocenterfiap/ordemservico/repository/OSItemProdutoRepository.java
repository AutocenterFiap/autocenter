package br.com.autocenterfiap.ordemservico.repository;

import br.com.autocenterfiap.ordemservico.repository.entity.OSItemProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OSItemProdutoRepository extends JpaRepository<OSItemProduto, Long> {

    List<OSItemProduto> findByOrdemServicoId(Long ordemServicoId);

    Optional<OSItemProduto> findByOrdemServicoIdAndProdutoId(Long ordemServicoId, Long produtoId);

    @Query("""
            SELECT COALESCE(SUM(i.precoUnitarioNoMomento * i.quantidade), 0)
            FROM OSItemProduto i
            WHERE i.ordemServico.id = :ordemServicoId
            """)
    BigDecimal calcularTotalProdutosPorOS(@Param("ordemServicoId") Long ordemServicoId);
}
