package br.com.autocenterfiap.veiculo.repository;

import br.com.autocenterfiap.veiculo.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo,Long> {
    Optional<Veiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    boolean existsByPlacaAndIdNot(String placa,Long id);
    boolean existsByRenavam(String renavam);
    boolean existsByRenavamAndIdNot(String renavam,Long id);
    boolean existsByChassi(String chassi);
    boolean existsByChassiAndIdNot(String chassi,Long id);
}
