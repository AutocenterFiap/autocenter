package br.com.autocenterfiap.produto.adapter.in.dto;

import br.com.autocenterfiap.produto.domain.enums.StatusEstoque;
import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.application.dto.ProdutoOutput;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de resposta de um produto/insumo")
public record ProdutoResponseDTO(

        @Schema(description = "ID do produto", example = "1")
        Long id,

        @Schema(description = "Nome do produto", example = "Filtro de Óleo")
        String nome,

        @Schema(description = "Código interno do produto", example = "FO-001")
        String codigo,

        @Schema(description = "Descrição do produto")
        String descricao,

        @Schema(description = "Unidade de medida", example = "UNIT")
        UnidadeMedida unidadeMedida,

        @Schema(description = "Preço unitário", example = "45.90")
        BigDecimal precoUnitario,

        @Schema(description = "Quantidade em estoque", example = "100")
        Integer quantidadeEstoque,

        @Schema(description = "Estoque mínimo", example = "10")
        Integer estoqueMinimo,

        @Schema(description = "Categoria do produto", example = "Motor")
        String categoria,

        @Schema(description = "Tipo do produto: PECAS ou INSUMOS", example = "PECAS")
        TipoProduto tipo,

        @Schema(description = "Status do estoque", example = "NORMAL")
        StatusEstoque statusEstoque,

        @Schema(description = "Indica se o produto está ativo", example = "true")
        Boolean ativo,

        @Schema(description = "Data de cadastro")
        LocalDateTime dataCriacao,

        @Schema(description = "Data da última atualização")
        LocalDateTime dataUltimaAtualizacao
) {
    public static ProdutoResponseDTO from(ProdutoOutput output) {
        if (output == null) return null;

        // Calculate statusEstoque based on quantity and minimum
        StatusEstoque calculatedStatus;
        if (output.getQuantidadeEstoque() == 0) {
            calculatedStatus = StatusEstoque.OUT_OF_STOCK;
        } else if (output.getQuantidadeEstoque() < output.getEstoqueMinimo()) {
            calculatedStatus = StatusEstoque.LOW_STOCK;
        } else {
            calculatedStatus = StatusEstoque.NORMAL;
        }

        return new ProdutoResponseDTO(
                output.getId(),
                output.getNome(),
                output.getCodigo(),
                output.getDescricao(),
                output.getUnidadeMedida(),
                output.getPrecoUnitario(),
                output.getQuantidadeEstoque(),
                output.getEstoqueMinimo(),
                output.getCategoria(),
                output.getTipo(),
                calculatedStatus,
                output.getAtivo(),
                output.getDataCriacao(),
                output.getDataUltimaAtualizacao()
        );
    }
}
