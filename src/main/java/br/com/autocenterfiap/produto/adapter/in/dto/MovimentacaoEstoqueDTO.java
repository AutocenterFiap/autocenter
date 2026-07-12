package br.com.autocenterfiap.produto.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para movimentação de estoque (entrada ou saída)")
public record MovimentacaoEstoqueDTO(

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        @Schema(description = "Quantidade a movimentar", example = "20")
        Integer quantidade,

        @NotBlank(message = "Motivo é obrigatório")
        @Schema(description = "Motivo da movimentação", example = "Reposição de estoque mensal")
        String motivo
) {
}
