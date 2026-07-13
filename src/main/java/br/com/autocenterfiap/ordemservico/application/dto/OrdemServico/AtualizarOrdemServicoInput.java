package br.com.autocenterfiap.ordemservico.application.dto.OrdemServico;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import jakarta.validation.constraints.NotNull;

public record AtualizarOrdemServicoInput(
        @NotNull(message = "O Status da OS é obrigatório")
        StatusOS statusOS
) {
}
