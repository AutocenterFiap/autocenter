package br.com.autocenterfiap.ordemservico.application.dto.OSItemServico;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record OSItemServicoInput(
        @NotNull(message = "ID do serviço é obrigatório")
        @Schema(description = "Identificador do serviço", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long servicoId
) {
}
