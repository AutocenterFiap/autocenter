package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.mapper.OrcamentoApplicationMapper;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.application.dto.PageResult;
import br.com.autocenterfiap.orcamento.application.dto.PaginationRequest;

public class BuscarTodosOrcamentosUseCase {

    private final OrcamentoRepositoryPort orcamentoRepositoryPort;

    public BuscarTodosOrcamentosUseCase(OrcamentoRepositoryPort orcamentoRepositoryPort) {
        this.orcamentoRepositoryPort = orcamentoRepositoryPort;
    }

    public PageResult<OrcamentoOutput> executar(StatusOrcamento statusOrcamento, PaginationRequest pagination) {
        return this.orcamentoRepositoryPort.buscarPorStatusOrcamento(statusOrcamento, pagination)
                .map(OrcamentoApplicationMapper::toOutput);
    }

}
