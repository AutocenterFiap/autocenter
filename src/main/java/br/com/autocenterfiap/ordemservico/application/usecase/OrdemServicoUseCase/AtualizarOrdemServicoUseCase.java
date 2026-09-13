package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.AtualizarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.mapper.OrdemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.ObservabilidadePort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;

import java.time.Duration;
import java.time.LocalDateTime;

public class AtualizarOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort repositoryPort;
    private final ObservabilidadePort observabilidadePort;

    public AtualizarOrdemServicoUseCase(OrdemServicoRepositoryPort repositoryPort, ObservabilidadePort observabilidadePort) {
        this.repositoryPort = repositoryPort;
        this.observabilidadePort = observabilidadePort;
    }

    public OrdemServicoOutput executar(Long id, AtualizarOrdemServicoInput ordemServicoInput) {

        OrdemServico ordemServico = this.repositoryPort.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada para o id: " + id));

        StatusOS statusAntigo = ordemServico.getStatusOS();
        StatusOS novoStatus = ordemServicoInput.statusOS();

        ordemServico.getStatusOS().podeMudarPara(novoStatus);
        ordemServico.setStatusOS(novoStatus);

        // Calcula a duração no status anterior se houver uma data de referência
        LocalDateTime inicioStatus = ordemServico.getDataUltimaAtualizacao() != null 
                ? ordemServico.getDataUltimaAtualizacao() 
                : ordemServico.getDataCriacao();

        if (inicioStatus != null && statusAntigo != novoStatus) {
            double segundos = Duration.between(inicioStatus, LocalDateTime.now()).toMillis() / 1000.0;
            if (this.observabilidadePort != null) {
                this.observabilidadePort.registrarTempoStatus(statusAntigo, segundos);
            }
        }

        OrdemServico ordemServicoAtualizada =  this.repositoryPort.save(ordemServico);

        return OrdemServicoApplicationMapper.toOutput(ordemServicoAtualizada);

    }
}