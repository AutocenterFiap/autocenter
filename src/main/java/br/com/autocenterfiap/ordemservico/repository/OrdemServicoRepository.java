package br.com.autocenterfiap.ordemservico.repository;

import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    @Query("SELECT os FROM OrdemServico os WHERE os.statusOS = :statusOS")
    List<OrdemServico> findByStatus(StatusOS statusOS);
    Optional<OrdemServico> findByNumeroOrdemServico(Long numeroOrdemServico);
}
