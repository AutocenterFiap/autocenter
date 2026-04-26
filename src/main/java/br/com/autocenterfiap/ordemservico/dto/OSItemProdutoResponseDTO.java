package br.com.autocenterfiap.ordemservico.dto;

import br.com.autocenterfiap.ordemservico.model.OSItemProduto;
import java.math.BigDecimal;

public record OSItemProdutoResponseDTO(
    Long id,
    BigDecimal valorItemProduto,
    Long quantidadeItem
) {
    public OSItemProdutoResponseDTO(OSItemProduto model) {
        this(
            model.getId(),
            model.getValorItemProduto(),
            model.getQuantidadeItem()
        );
    }
}
