package br.com.autocenterfiap.ordemservico.dto;

import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemServicoResponseDTO(
    Long id,
    Long numeroOrdemServico,
    StatusOS status,
    BigDecimal valorTotal,
    Long veiculoId,
    Long clienteId
) {
    public OrdemServicoResponseDTO(OrdemServico ordemServico) {
        this(
            ordemServico.getId(),
            ordemServico.getNumeroOrdemServico(),
            ordemServico.getStatus(),
            ordemServico.getValorTotal(),
            ordemServico.getVeiculo().getId(),
            ordemServico.getCliente().getId()
        );
    }
}
