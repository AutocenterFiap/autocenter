package br.com.autocenterfiap.ordemservico.dto;

import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import jakarta.validation.constraints.NotNull;

public record OrdemServicoUpdateDTO(
    @NotNull(message = "O Status da OS é obrigatório")
    StatusOS statusOS
) {
}
