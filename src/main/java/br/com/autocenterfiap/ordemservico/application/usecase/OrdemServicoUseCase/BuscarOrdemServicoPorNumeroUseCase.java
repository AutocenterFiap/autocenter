package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.mapper.OrdemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import org.springframework.transaction.annotation.Transactional;

public class BuscarOrdemServicoPorNumeroUseCase {

    private final OrdemServicoRepositoryPort repositoryPort;


    public BuscarOrdemServicoPorNumeroUseCase(OrdemServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Transactional(readOnly = true)
    public OrdemServicoOutput executar(Long numero) {
        OrdemServico ordemServico = this.repositoryPort.findByNumeroOrdemServico(numero)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada para o número: " + numero));

        return OrdemServicoApplicationMapper.toOutput(ordemServico);

    }
}
