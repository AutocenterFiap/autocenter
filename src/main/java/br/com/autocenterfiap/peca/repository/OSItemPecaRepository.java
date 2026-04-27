package br.com.autocenterfiap.peca.repository;

import br.com.autocenterfiap.peca.model.OSItemPeca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OSItemPecaRepository extends JpaRepository<OSItemPeca, Long> {

    List<OSItemPeca> findByOrdemServicoId(Long ordemServicoId);

    Optional<OSItemPeca> findByOrdemServicoIdAndPecaId(Long ordemServicoId, Long pecaId);

    @Query("""
            SELECT COALESCE(SUM(i.precoUnitarioNoMomento * i.quantidade), 0)
            FROM OSItemPeca i
            WHERE i.ordemServicoId = :ordemServicoId
            """)
    BigDecimal calcularTotalPecasPorOS(@Param("ordemServicoId") Long ordemServicoId);
}
