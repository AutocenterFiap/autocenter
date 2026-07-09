package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.dto.PageResult;
import br.com.autocenterfiap.servico.application.dto.PaginationRequest;
import br.com.autocenterfiap.servico.application.dto.ServicoOutput;
import br.com.autocenterfiap.servico.application.mapper.ServicoApplicationMapper;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;

public class ListarServicosPorStatusUseCase {

    private final ServicoRepositoryPort repositoryPort;

    public ListarServicosPorStatusUseCase(ServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public PageResult<ServicoOutput> executar(StatusServico status, PaginationRequest pagination) {
        return repositoryPort.listarPorStatus(status, pagination)
                .map(ServicoApplicationMapper::toOutput);
    }
}
