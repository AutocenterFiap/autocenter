package br.com.autocenterfiap.ordemservico.application.dto.OrdemServico;

import jakarta.validation.constraints.NotNull;

public record CriarOrdemServicoInput (
        @NotNull(message = "O ID do veículo é obrigatório")
        Long veiculoId,

        @NotNull(message = "O ID do cliente é obrigatório")
        Long clienteId
) {}
