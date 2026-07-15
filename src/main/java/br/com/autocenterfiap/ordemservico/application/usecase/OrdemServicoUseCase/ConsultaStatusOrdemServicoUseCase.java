package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.ConsultaStatusOrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;

public class ConsultaStatusOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort repositoryPort;

    public ConsultaStatusOrdemServicoUseCase(OrdemServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public ConsultaStatusOrdemServicoOutput executar(Long id) {

        OrdemServico ordemServico = this.repositoryPort.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada para o id: "
                        + id));

        return new ConsultaStatusOrdemServicoOutput(ordemServico.getStatusOS());
    }
}
