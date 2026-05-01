package br.com.autocenterfiap.ordemservico.dto;

import br.com.autocenterfiap.ordemservico.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.model.OSItemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record OSItemServicoResponseDTO(
    Long id,
    BigDecimal valorItemServico,
    StatusItemServico statusItemServico,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraFim
) {
    public OSItemServicoResponseDTO(OSItemServico model) {
        this(
            model.getId(),
            model.getValorItemServico(),
            model.getStatusServico(),
            model.getDataHoraInicio(),
            model.getDataHoraFim()
        );
    }
}
