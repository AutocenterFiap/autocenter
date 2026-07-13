package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;

public class DeletarOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort repositoryPort;

    public DeletarOrdemServicoUseCase(OrdemServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public void executar(Long id) {
        OrdemServico ordemServico = this.repositoryPort.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada para o id: "
                        + id));

        this.repositoryPort.delete(ordemServico);
    }
}
