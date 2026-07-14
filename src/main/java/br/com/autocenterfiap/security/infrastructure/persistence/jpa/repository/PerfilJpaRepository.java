package br.com.autocenterfiap.security.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilJpaRepository extends JpaRepository<PerfilJpaEntity, Long> {

    Optional<PerfilJpaEntity> findByNome(PerfilType nome);
}

