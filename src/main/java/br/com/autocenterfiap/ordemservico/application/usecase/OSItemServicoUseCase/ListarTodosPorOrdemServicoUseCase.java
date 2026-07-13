package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class ListarTodosPorOrdemServicoUseCase {

    private final OSItemServicoRepositoryPort itemServicoRepositoryPort;

    public ListarTodosPorOrdemServicoUseCase(OSItemServicoRepositoryPort itemServicoRepositoryPort) {
        this.itemServicoRepositoryPort = itemServicoRepositoryPort;
    }

    @Transactional(readOnly = true)
    public PageResult<OSItemServicoOutput> executar(Long ordermServicoId, PaginationRequest pagination) {
        return this.itemServicoRepositoryPort.findByOrdemServicoId(ordermServicoId ,pagination)
                .map(OSItemServicoApplicationMapper::toOutput);
    }
}
