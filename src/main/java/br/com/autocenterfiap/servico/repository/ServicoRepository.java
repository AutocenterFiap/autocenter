package br.com.autocenterfiap.servico.repository;

import br.com.autocenterfiap.servico.enums.StatusServico;
import br.com.autocenterfiap.servico.model.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    Page<Servico> findAllByStatus(StatusServico status, Pageable pageable);
}

