package br.com.autocenterfiap.ordemservico.adapter.in.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record OrdemServicoDTO(
    @NotNull(message = "O ID do veículo é obrigatório")
    Long veiculoId,
    
    @NotNull(message = "O ID do cliente é obrigatório")
    Long clienteId,

    @NotNull(message = "A lista de IDs de serviços é obrigatório")
    List<Long> servicosIds,

    @NotNull(message = "A lista de IDs de produtos é obrigatório")
    Map<Long, Integer> produtosIdsAndQuantidades
) {
}
