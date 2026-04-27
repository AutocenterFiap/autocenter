package br.com.autocenterfiap.peca.dto;

import br.com.autocenterfiap.peca.enums.StatusEstoque;
import br.com.autocenterfiap.peca.enums.TipoPeca;
import br.com.autocenterfiap.peca.enums.UnidadeMedida;
import br.com.autocenterfiap.peca.model.Peca;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de resposta de uma peça/insumo")
public record PecaResponseDTO(

        @Schema(description = "ID da peça", example = "1")
        Long id,

        @Schema(description = "Nome da peça", example = "Filtro de Óleo")
        String nome,

        @Schema(description = "Código interno da peça", example = "FO-001")
        String codigo,

        @Schema(description = "Descrição da peça")
        String descricao,

        @Schema(description = "Unidade de medida", example = "UNIT")
        UnidadeMedida unidadeMedida,

        @Schema(description = "Preço unitário", example = "45.90")
        BigDecimal precoUnitario,

        @Schema(description = "Quantidade em estoque", example = "100")
        Integer quantidadeEstoque,

        @Schema(description = "Estoque mínimo", example = "10")
        Integer estoqueMinimo,

        @Schema(description = "Categoria da peça", example = "Motor")
        String categoria,

        @Schema(description = "Tipo do item: PECAS ou INSUMOS", example = "PECAS")
        TipoPeca tipo,

        @Schema(description = "Status do estoque", example = "NORMAL")
        StatusEstoque statusEstoque,

        @Schema(description = "Indica se a peça está ativa", example = "true")
        Boolean ativo,

        @Schema(description = "Data de cadastro")
        LocalDateTime dataCriacao,

        @Schema(description = "Data da última atualização")
        LocalDateTime dataUltimaAtualizacao
) {
    public static PecaResponseDTO from(Peca peca) {
        return new PecaResponseDTO(
                peca.getId(),
                peca.getNome(),
                peca.getCodigo(),
                peca.getDescricao(),
                peca.getUnidadeMedida(),
                peca.getPrecoUnitario(),
                peca.getQuantidadeEstoque(),
                peca.getEstoqueMinimo(),
                peca.getCategoria(),
                peca.getTipo(),
                peca.getStatusEstoque(),
                peca.getAtivo(),
                peca.getDataCriacao(),
                peca.getDataUltimaAtualizacao()
        );
    }
}
