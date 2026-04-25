package br.com.autocenterfiap.orcamento.repository;

import br.com.autocenterfiap.orcamento.repository.entity.Orcamento;
import br.com.autocenterfiap.ordemservico.repository.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    @Query("SELECT o FROM Orcamento o WHERE o.statusOrcamento = 'AGUARDANDO_APROVACAO' and o.ordemServico = :os")
    Optional<Orcamento> buscarOrcamentoAguardandoAprovacaoPorOS(OrdemServico os);
}
