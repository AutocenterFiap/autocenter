package br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, Long> {

    Optional<ClienteJpaEntity> findByDocumento(String documento);

    Optional<ClienteJpaEntity> findByEmail(String email);

    boolean existsByDocumento(String documento);

    boolean existsByEmail(String email);
}

