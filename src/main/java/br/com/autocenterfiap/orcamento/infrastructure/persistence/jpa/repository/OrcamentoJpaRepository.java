package br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.entity.OrcamentoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrcamentoJpaRepository extends JpaRepository<OrcamentoJpaEntity, Long> {

    @Query("SELECT o FROM OrcamentoJpaEntity o WHERE o.statusOrcamento = 'AGUARDANDO_APROVACAO' and o.ordemServicoJpaEntity = :os")
    Optional<OrcamentoJpaEntity> buscarOrcamentoAguardandoAprovacaoPorOS(OrdemServicoJpaEntity os);

    Page<OrcamentoJpaEntity> findByStatusOrcamento(StatusOrcamento status, Pageable pageable);
}
