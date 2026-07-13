package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.mapper.OrdemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import org.springframework.transaction.annotation.Transactional;

public class BuscarOrdemServicoPorIdUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public BuscarOrdemServicoPorIdUseCase(OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
    }

    @Transactional(readOnly = true)
    public OrdemServicoOutput executar(Long id) {
        OrdemServico ordemServico = this.ordemServicoRepositoryPort.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada para o id: " + id));

        return OrdemServicoApplicationMapper.toOutput(ordemServico);
    }
}
