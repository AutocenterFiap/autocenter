package br.com.autocenterfiap.ordemservico.dto;

import jakarta.validation.constraints.NotNull;

public record OrdemServicoDTO(
    @NotNull(message = "O ID do veículo é obrigatório")
    Long veiculoId,
    
    @NotNull(message = "O ID do cliente é obrigatório")
    Long clienteId
) {
}
