package br.com.autocenterfiap.produto.dto;

import br.com.autocenterfiap.produto.enums.TipoProduto;
import br.com.autocenterfiap.produto.enums.UnidadeMedida;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Dados para criação ou atualização de um produto/insumo")
public record ProdutoRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Schema(description = "Nome do produto", example = "Filtro de Óleo")
        String nome,

        @NotBlank(message = "Código é obrigatório")
        @Schema(description = "Código interno único do produto", example = "FO-001")
        String codigo,

        @Schema(description = "Descrição do produto", example = "Filtro de óleo para motores 1.0 a 2.0")
        String descricao,

        @NotNull(message = "Unidade de medida é obrigatória")
        @Schema(description = "Unidade de medida: UNIT, LITER, METER, KG, BOX", example = "UNIT")
        UnidadeMedida unidadeMedida,

        @NotNull(message = "Preço unitário é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
        @Schema(description = "Preço unitário", example = "45.90")
        BigDecimal precoUnitario,

        @NotNull(message = "Quantidade em estoque é obrigatória")
        @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
        @Schema(description = "Quantidade inicial em estoque", example = "100")
        Integer quantidadeEstoque,

        @NotNull(message = "Estoque mínimo é obrigatório")
        @Min(value = 0, message = "Estoque mínimo não pode ser negativo")
        @Schema(description = "Quantidade mínima de estoque para alerta", example = "10")
        Integer estoqueMinimo,

        @NotBlank(message = "Categoria é obrigatória")
        @Schema(description = "Categoria do produto", example = "Motor")
        String categoria,

        @NotNull(message = "Tipo é obrigatório")
        @Schema(description = "Tipo do produto: PECAS ou INSUMOS", example = "PECAS")
        TipoProduto tipo
) {
}
