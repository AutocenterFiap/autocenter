package br.com.autocenterfiap.ordemservico.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para requisição de item de serviço da ordem de serviço")
public record OSItemServicoRequestDTO(

        @NotNull(message = "ID do serviço é obrigatório")
        @Schema(description = "Identificador do serviço", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long servicoId

) {}