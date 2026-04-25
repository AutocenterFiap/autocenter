package br.com.autocenterfiap.orcamento.repository;

import br.com.autocenterfiap.orcamento.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

}
