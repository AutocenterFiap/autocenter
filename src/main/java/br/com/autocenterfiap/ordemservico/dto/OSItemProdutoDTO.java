package br.com.autocenterfiap.ordemservico.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record OSItemProdutoDTO(
    // Long produtoId, // TODO: Descomentar quando existir a entidade Produto

    @PositiveOrZero(message = "O valor do item não pode ser negativo")
    BigDecimal valorItemProduto,

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    Long quantidadeItem
) {
}
