package br.com.autocenterfiap.ordemservico.repository;

import br.com.autocenterfiap.ordemservico.model.OSItemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OSItemServicoRepository extends JpaRepository<OSItemServico, Long> {

    List<OSItemServico> findByOrdemServicoId(Long ordemServicoId);

    Optional<OSItemServico> findByServicoIdAndOrdemServicoId(Long servicoId, Long ordemServicoId);
}
