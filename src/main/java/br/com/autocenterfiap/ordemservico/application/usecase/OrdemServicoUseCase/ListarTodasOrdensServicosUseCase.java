package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.mapper.OrdemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;

public class ListarTodasOrdensServicosUseCase {

    private final OrdemServicoRepositoryPort repositoryPort;

    public ListarTodasOrdensServicosUseCase(OrdemServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public PageResult<OrdemServicoOutput> executar(PaginationRequest pagination) {

        return this.repositoryPort.findAll(pagination)
                .map(OrdemServicoApplicationMapper::toOutput);
    }
}
