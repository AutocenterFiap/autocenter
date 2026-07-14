package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OSItemServicoNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSItemInvalidoException;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class IniciarServicoUseCase {

    private final OSItemServicoRepositoryPort itemServicoRepositoryPort;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public IniciarServicoUseCase(
            OSItemServicoRepositoryPort itemServicoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort)
    {
        this.itemServicoRepositoryPort = itemServicoRepositoryPort;
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
    }

    public OSItemServicoOutput executar(Long ordemServicoId, Long servicoId) {
        log.info("Iniciando execução do serviço: OS ID={}, Serviço ID={}", ordemServicoId, servicoId);

        OrdemServico ordemServico = this.ordemServicoRepositoryPort.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada para o Id "
                        + ordemServicoId));

        if (ordemServico.getStatusOS() != StatusOS.EM_EXECUCAO) {
            log.warn("Tentativa de iniciar serviço em OS com status inválido: OS ID={}, Status Atual={}",
                    ordemServicoId, ordemServico.getStatusOS());
            throw new StatusOSInvalidoException("Só é possível iniciar serviços em ordens de serviço que estejam " +
                    "no status " + StatusOS.EM_EXECUCAO.name());
        }

        OSItemServico osItemServico = this.itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(servicoId, ordemServicoId)
                .orElseThrow(() -> new OSItemServicoNaoEncontradoException(ordemServicoId, servicoId));

        if (osItemServico.getStatusServico() != StatusItemServico.AGUARDANDO_INICIO) {
            log.warn("Tentativa de iniciar serviço com status inválido: Item ID={}, Status Atual={}",
                    osItemServico.getId(), osItemServico.getStatusServico());
            throw new StatusOSItemInvalidoException("Só é possível iniciar um serviço que esteja no status "
                    + StatusItemServico.AGUARDANDO_INICIO.name());
        }

        osItemServico.setStatusServico(StatusItemServico.EXECUTANDO);
        osItemServico.setDataHoraInicio(LocalDateTime.now());

        OSItemServico itemAtualizado = this.itemServicoRepositoryPort.save(osItemServico);

        log.info("Serviço iniciado: Item ID={}, Status={}", osItemServico.getId(), StatusItemServico.EXECUTANDO);

        return OSItemServicoApplicationMapper.toOutput(itemAtualizado);
    }
}
