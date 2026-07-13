package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OSItemServicoNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class FinalizarServicoUseCase {

    private final OSItemServicoRepositoryPort itemServicoRepositoryPort;

    public FinalizarServicoUseCase(OSItemServicoRepositoryPort itemServicoRepositoryPort) {
        this.itemServicoRepositoryPort = itemServicoRepositoryPort;
    }

    @Transactional
    public OSItemServicoOutput executar(Long servicoId, Long ordemServicoId) {

        OSItemServico osItemServico = this.itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(servicoId, ordemServicoId)
                .orElseThrow(() -> new OSItemServicoNaoEncontradoException(ordemServicoId, servicoId));

        if (osItemServico.getStatusServico() != StatusItemServico.EXECUTANDO) {
            log.warn("Tentativa de finalizar serviço com status inválido: Item ID={}, Status Atual={}",
                    osItemServico.getId(), osItemServico.getStatusServico());
            throw new StatusOSInvalidoException("Só é possível finalizar um serviço que esteja no status 'EXECUTANDO'");
        }

        osItemServico.setStatusServico(StatusItemServico.FINALIZADO);
        osItemServico.setDataHoraFim(LocalDateTime.now());

        OSItemServico itemAtualizado = this.itemServicoRepositoryPort.save(osItemServico);

        log.info("Serviço finalizado: Item ID={}, Status={}, Duração={}min",
                itemAtualizado.getId(), StatusItemServico.FINALIZADO,
                Duration.between(itemAtualizado.getDataHoraInicio(), itemAtualizado.getDataHoraFim()).toMinutes());

        return OSItemServicoApplicationMapper.toOutput(itemAtualizado);
    }
}
