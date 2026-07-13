package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.AtualizarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.mapper.OrdemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import org.springframework.transaction.annotation.Transactional;

public class AtualizarOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort repositoryPort;

    public AtualizarOrdemServicoUseCase(OrdemServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Transactional
    public OrdemServicoOutput executar(Long id, AtualizarOrdemServicoInput ordemServicoInput) {

        OrdemServico ordemServico = this.repositoryPort.findById(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada para o id: " + id));

        StatusOS novoStatus = ordemServicoInput.statusOS();

        ordemServico.getStatusOS().podeMudarPara(novoStatus);
        ordemServico.setStatusOS(novoStatus);

        OrdemServico ordemServicoAtualizada =  this.repositoryPort.save(ordemServico);

        return OrdemServicoApplicationMapper.toOutput(ordemServicoAtualizada);

    }
}