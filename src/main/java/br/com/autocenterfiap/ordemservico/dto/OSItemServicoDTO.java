package br.com.autocenterfiap.ordemservico.dto;

import br.com.autocenterfiap.ordemservico.enums.StatusItemServico;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OSItemServicoDTO(
    // Long servicoId, // TODO: Descomentar quando existir a entidade Servico

    @PositiveOrZero(message = "O valor do serviço não pode ser negativo")
    BigDecimal valorItemServico,

    StatusItemServico statusItemServico,

    @NotNull(message = "Data de início é obrigatória")
    LocalDateTime dataHoraInicio,

    LocalDateTime dataHoraFim
) {
}
