package br.com.autocenterfiap.ordemservico.adapter.in.dto;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;

import java.math.BigDecimal;

public record OrdemServicoResponseDTO(
    Long id,
    Long numeroOrdemServico,
    StatusOS statusOS,
    BigDecimal valorTotal,
    Long veiculoId,
    Long clienteId
) {
    public OrdemServicoResponseDTO(OrdemServicoJpaEntity ordemServicoJpaEntity) {
        this(
            ordemServicoJpaEntity.getId(),
            ordemServicoJpaEntity.getNumeroOrdemServico(),
            ordemServicoJpaEntity.getStatusOS(),
            ordemServicoJpaEntity.getValorTotal(),
            ordemServicoJpaEntity.getVeiculo().getId(),
            ordemServicoJpaEntity.getCliente().getId()
        );
    }
}
