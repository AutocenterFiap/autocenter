package br.com.autocenterfiap.ordemservico.dto;

import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;

import java.math.BigDecimal;

public record OrdemServicoResponseDTO(
    Long id,
    Long numeroOrdemServico,
    StatusOS statusOS,
    BigDecimal valorTotal,
    Long veiculoId,
    Long clienteId
) {
    public OrdemServicoResponseDTO(OrdemServico ordemServico) {
        this(
            ordemServico.getId(),
            ordemServico.getNumeroOrdemServico(),
            ordemServico.getStatusOS(),
            ordemServico.getValorTotal(),
            ordemServico.getVeiculo().getId(),
            ordemServico.getCliente().getId()
        );
    }
}
