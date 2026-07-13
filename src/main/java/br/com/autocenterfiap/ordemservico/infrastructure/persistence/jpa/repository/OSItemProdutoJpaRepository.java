package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemProdutoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OSItemProdutoJpaRepository extends JpaRepository<OSItemProdutoJpaEntity, Long> {

    Page<OSItemProdutoJpaEntity> findByOrdemServicoJpaEntityId(Long ordemServicoId, Pageable pageable);

    Optional<OSItemProdutoJpaEntity> findByOrdemServicoJpaEntityIdAndProdutoId(Long ordemServicoId, Long produtoId);

    @Query("""
            SELECT COALESCE(SUM(i.precoUnitarioNoMomento * i.quantidade), 0)
            FROM OSItemProdutoJpaEntity i
            WHERE i.ordemServicoJpaEntity.id = :ordemServicoId
            """)
    BigDecimal calcularTotalProdutosPorOS(@Param("ordemServicoId") Long ordemServicoId);
}
