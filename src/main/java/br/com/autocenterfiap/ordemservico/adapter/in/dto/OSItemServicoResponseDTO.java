package br.com.autocenterfiap.ordemservico.adapter.in.dto;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemServicoJpaEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record OSItemServicoResponseDTO(
    Long id,
    BigDecimal valorItemServico,
    StatusItemServico statusItemServico,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraFim
) {
    public OSItemServicoResponseDTO(OSItemServicoJpaEntity model) {
        this(
            model.getId(),
            model.getValorItemServico(),
            model.getStatusServico(),
            model.getDataHoraInicio(),
            model.getDataHoraFim()
        );
    }
}
