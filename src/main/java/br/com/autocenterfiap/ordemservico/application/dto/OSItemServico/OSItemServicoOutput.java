package br.com.autocenterfiap.ordemservico.application.dto.OSItemServico;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OSItemServicoOutput (
        Long id,
        BigDecimal valorItemServico,
        StatusItemServico statusItemServico,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim
) {
}
