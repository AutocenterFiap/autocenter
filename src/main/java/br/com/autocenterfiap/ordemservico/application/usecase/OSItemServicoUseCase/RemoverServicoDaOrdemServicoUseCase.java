package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.exception.OSItemServicoNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSItemInvalidoException;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.util.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoverServicoDaOrdemServicoUseCase {

    private final OSItemServicoRepositoryPort itemServicoRepositoryPort;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public RemoverServicoDaOrdemServicoUseCase(
            OSItemServicoRepositoryPort itemServicoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort)
    {
        this.itemServicoRepositoryPort = itemServicoRepositoryPort;
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
    }

    public void executar(Long ordemServicoId, Long servicoId) {
        log.info("Removendo serviço da OS: OS ID={}, Serviço ID={}", ordemServicoId, servicoId);

        OrdemServico ordemServico = this.ordemServicoRepositoryPort.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada com ID: "
                        + ordemServicoId));

        if (ordemServico.getStatusOS() != StatusOS.EM_DIAGNOSTICO) {
            log.warn("Tentativa de remover serviço em OS com status inválido: OS ID={}, Status Atual={}",
                    ordemServicoId, ordemServico.getStatusOS());
            throw new StatusOSInvalidoException("Só é possível remover serviços de ordens de serviço que estejam no status "
                    + StatusOS.EM_DIAGNOSTICO.name());
        }

        OSItemServico itemServico = this.itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(servicoId, ordemServicoId)
                .orElseThrow(() -> new OSItemServicoNaoEncontradoException(ordemServicoId, servicoId));

        if (itemServico.getStatusServico() != StatusItemServico.AGUARDANDO_INICIO) {
            log.warn("Tentativa de remover serviço com status inválido: Item ID={}, Status Atual={}",
                    itemServico.getId(), itemServico.getStatusServico());
            throw new StatusOSItemInvalidoException("Só é possível remover um serviço que esteja no status "
                    + StatusItemServico.AGUARDANDO_INICIO.name());
        }

        // Recalcula o valor total da OS após remover o serviço
        ordemServico.getOsItensServicos().remove(itemServico);
        ordemServico.setValorTotal(Util.calcularValorTotal(ordemServico));

        log.info("Serviço removido da OS com sucesso: Item ID={}, Serviço={}",
                itemServico.getId(), itemServico.getServico().getDescricao());

    }
}
