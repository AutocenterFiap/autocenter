package br.com.autocenterfiap.orcamento.dto;

import br.com.autocenterfiap.orcamento.enums.TipoEnvio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record EnvioRequest(
        @NotNull(message = "Tipo de envio é obrigatório")
        @Schema(
                description = "Tipo de envio: EMAIL, WHATSAPP, IMPRESSORA",
                example = "EMAIL",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"EMAIL", "WHATSAPP", "IMPRESSORA"}
        )
        TipoEnvio tipo) {}

