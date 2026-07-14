package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoJpaEntity, Long> {

    @Query("SELECT os FROM OrdemServicoJpaEntity os WHERE os.statusOS = :statusOS")
    Page<OrdemServicoJpaEntity> findByStatus(StatusOS statusOS, Pageable pageable);

    @Query("""
            SELECT os FROM OrdemServicoJpaEntity os
            WHERE os.statusOS NOT IN (
                br.com.autocenterfiap.ordemservico.domain.enums.StatusOS.FINALIZADA,
                br.com.autocenterfiap.ordemservico.domain.enums.StatusOS.ENTREGUE
            )
            ORDER BY
                CASE os.statusOS
                    WHEN br.com.autocenterfiap.ordemservico.domain.enums.StatusOS.EM_EXECUCAO THEN 1
                    WHEN br.com.autocenterfiap.ordemservico.domain.enums.StatusOS.AGUARDANDO_APROVACAO THEN 2
                    WHEN br.com.autocenterfiap.ordemservico.domain.enums.StatusOS.EM_DIAGNOSTICO THEN 3
                    WHEN br.com.autocenterfiap.ordemservico.domain.enums.StatusOS.RECEBIDA THEN 4
                    ELSE 5
                END,
                os.dataCriacao ASC,
                os.id ASC
            """)
    Page<OrdemServicoJpaEntity> findAtivasOrdenadasPorPrioridade(Pageable pageable);

    boolean existsByClienteId(Long clienteId);

    boolean existsByVeiculoIdAndStatusOSIn(Long veiculoId, List<StatusOS> statusList);

    boolean existsByVeiculoId(Long veiculoId);

    Optional<OrdemServicoJpaEntity> findByNumeroOrdemServico(Long numeroOrdemServico);
}

