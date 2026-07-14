package br.com.autocenterfiap.produto.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para vincular um produto a uma Ordem de Serviço")
public record OSItemProdutoRequestDTO(

        @NotNull(message = "ID do produto é obrigatório")
        @Schema(description = "ID do produto a ser vinculado", example = "1")
        Long produtoId,

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        @Schema(description = "Quantidade do produto a utilizar", example = "2")
        Integer quantidade
) {
}
