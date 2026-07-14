package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.dto.PageResult;
import br.com.autocenterfiap.servico.application.dto.PaginationRequest;
import br.com.autocenterfiap.servico.application.dto.ServicoOutput;
import br.com.autocenterfiap.servico.application.mapper.ServicoApplicationMapper;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;

public class ListarServicosUseCase {

    private final ServicoRepositoryPort repositoryPort;

    public ListarServicosUseCase(ServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public PageResult<ServicoOutput> executar(PaginationRequest pagination) {
        return repositoryPort.listarTodos(pagination)
                .map(ServicoApplicationMapper::toOutput);
    }
}
