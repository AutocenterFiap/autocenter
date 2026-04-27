package br.com.autocenterfiap.peca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para vincular uma peça a uma Ordem de Serviço")
public record OSItemPecaRequestDTO(

        @NotNull(message = "ID da peça é obrigatório")
        @Schema(description = "ID da peça a ser vinculada", example = "1")
        Long pecaId,

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        @Schema(description = "Quantidade da peça a utilizar", example = "2")
        Integer quantidade
) {
}
