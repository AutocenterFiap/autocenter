package br.com.autocenterfiap.ordemservico.adapter.in.dto;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import jakarta.validation.constraints.NotNull;

public record OrdemServicoUpdateDTO(
    @NotNull(message = "O Status da OS é obrigatório")
    StatusOS statusOS
) {
}
