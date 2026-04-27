package br.com.autocenterfiap.produto.dto;

import br.com.autocenterfiap.produto.enums.StatusEstoque;
import br.com.autocenterfiap.produto.enums.TipoProduto;
import br.com.autocenterfiap.produto.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.model.Produto;
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
    public static ProdutoResponseDTO from(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getCodigo(),
                produto.getDescricao(),
                produto.getUnidadeMedida(),
                produto.getPrecoUnitario(),
                produto.getQuantidadeEstoque(),
                produto.getEstoqueMinimo(),
                produto.getCategoria(),
                produto.getTipo(),
                produto.getStatusEstoque(),
                produto.getAtivo(),
                produto.getDataCriacao(),
                produto.getDataUltimaAtualizacao()
        );
    }
}
