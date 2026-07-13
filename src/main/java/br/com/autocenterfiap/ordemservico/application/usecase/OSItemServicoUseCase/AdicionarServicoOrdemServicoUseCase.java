package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import br.com.autocenterfiap.servico.domain.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;
import br.com.autocenterfiap.util.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdicionarServicoOrdemServicoUseCase {

    private final OSItemServicoRepositoryPort itemServicoRepositoryPort;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;
    private final ServicoRepositoryPort servicoRepositoryPort;

    public AdicionarServicoOrdemServicoUseCase(
            OSItemServicoRepositoryPort itemServicoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort,
            ServicoRepositoryPort servicoRepositoryPort)
    {
        this.itemServicoRepositoryPort = itemServicoRepositoryPort;
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
        this.servicoRepositoryPort = servicoRepositoryPort;
    }

    public OSItemServicoOutput executar(Long ordemServicoId, OSItemServicoInput itemServicoInput) {
        log.info("Adicionando serviço ID={} na Ordem de Serviço ID={}", itemServicoInput.servicoId(), ordemServicoId);

        OrdemServico ordemServico = this.ordemServicoRepositoryPort.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada para o id: "
                        + ordemServicoId));

        if (ordemServico.getStatusOS() != StatusOS.EM_DIAGNOSTICO) {
            log.warn("Tentativa de adicionar serviço em OS com status inválido: OS ID={}, Status Atual={}",
                    ordemServicoId, ordemServico.getStatusOS());
            throw new StatusOSInvalidoException("Só é possível adicionar serviços em ordens de serviço que estejam no " +
                    "status " + StatusOS.EM_DIAGNOSTICO.name());
        }

        Servico servico = this.servicoRepositoryPort.buscarPorId(itemServicoInput.servicoId())
                .orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado com ID: " + itemServicoInput.servicoId()));

        if (servico.getStatus() == StatusServico.INATIVO) {
            log.warn("Tentativa de adicionar serviço inativo: ID={}, Descrição={}",
                    servico.getId(), servico.getDescricao());
            throw new ServicoInativoException(servico.getDescricao());
        }

        OSItemServico item = new OSItemServico();

        item.setOrdemServico(ordemServico);
        item.setServico(servico);
        item.setValorItemServico(servico.getValor());
        item.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);

        // Recalcula o valo total da OS após adicionar o serviço
        ordemServico.getOsItensServicos().add(item);
        ordemServico.setValorTotal(Util.calcularValorTotal(ordemServico));

        OSItemServico itemSalvo = this.itemServicoRepositoryPort.save(item);

        log.info("Serviço adicionado com sucesso na OS: Item ID={}, Serviço={}, Valor={}",
                itemSalvo.getId(), servico.getDescricao(), servico.getValor());

        return OSItemServicoApplicationMapper.toOutput(itemSalvo);
    }
}
