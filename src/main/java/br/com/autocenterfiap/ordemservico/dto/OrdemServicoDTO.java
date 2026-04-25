package br.com.autocenterfiap.ordemservico.dto;

import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record OrdemServicoDTO(
    Long numeroOrdemServico,

    @NotNull(message = "O ID do veículo é obrigatório")
    Long veiculoId,
    
    @NotNull(message = "O ID do cliente é obrigatório")
    Long clienteId,
    
    StatusOS status,
    
    @PositiveOrZero(message = "O valor total não pode ser negativo")
    BigDecimal valorTotal
) {
}
