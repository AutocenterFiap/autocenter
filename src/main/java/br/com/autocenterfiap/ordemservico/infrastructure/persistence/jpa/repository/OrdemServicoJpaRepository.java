package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemServicoJpaRepository extends JpaRepository<Object, Long> {

    boolean existsByClienteId(Long clienteId);

}

