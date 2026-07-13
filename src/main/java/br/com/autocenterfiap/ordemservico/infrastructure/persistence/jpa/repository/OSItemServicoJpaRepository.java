package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemServicoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OSItemServicoJpaRepository extends JpaRepository<OSItemServicoJpaEntity, Long> {

    Page<OSItemServicoJpaEntity> findByOrdemServicoJpaEntityId(Long ordemServicoId,Pageable pageable);

    Page<OSItemServicoJpaEntity> findAllFinalizados(Pageable pageable);

    Optional<OSItemServicoJpaEntity> findByServicoIdAndOrdemServicoJpaEntityId(Long servicoId, Long ordemServicoId);

    boolean existsByServicoId(Long servicoId);
}
