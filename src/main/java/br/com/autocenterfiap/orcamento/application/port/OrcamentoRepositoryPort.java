package br.com.autocenterfiap.orcamento.application.port;

import br.com.autocenterfiap.orcamento.application.dto.PaginationRequest;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.application.dto.PageResult;

import java.util.Optional;

public interface OrcamentoRepositoryPort {

    Optional<Orcamento> buscarPorId(Long id);

    Optional<Orcamento> buscarOrcamentoAguardandoAprovacaoPorOS(Long os);

    PageResult<Orcamento> buscarPorStatusOrcamento(StatusOrcamento status, PaginationRequest pagination);

    Orcamento salvar(Orcamento orcamento);

}
