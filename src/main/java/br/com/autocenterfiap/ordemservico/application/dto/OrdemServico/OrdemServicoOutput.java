package br.com.autocenterfiap.ordemservico.application.dto.OrdemServico;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;

import java.math.BigDecimal;

public record OrdemServicoOutput (
        Long id,
        Long numeroOrdemServico,
        StatusOS statusOS,
        BigDecimal valorTotal,
        Long veiculoId,
        Long clienteId
) {
}
