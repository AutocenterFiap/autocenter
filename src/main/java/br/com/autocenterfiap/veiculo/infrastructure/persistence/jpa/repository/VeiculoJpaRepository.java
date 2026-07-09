package br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.entity.VeiculoJpaEntity;

import java.util.Optional;

@Repository
public interface VeiculoJpaRepository extends JpaRepository<VeiculoJpaEntity, Long> {

    Optional<VeiculoJpaEntity> findByPlaca(String placa);

    Optional<VeiculoJpaEntity> findByChassi(String chassi);

    Optional<VeiculoJpaEntity> findByRenavam(String renavam);

    boolean existsByPlaca(String placa);

    boolean existsByPlacaAndIdNot(String placa, Long id);

    boolean existsByChassi(String chassi);

    boolean existsByChassiAndIdNot(String chassi, Long id);

    boolean existsByRenavam(String renavam);

    boolean existsByRenavamAndIdNot(String renavam, Long id);
}
