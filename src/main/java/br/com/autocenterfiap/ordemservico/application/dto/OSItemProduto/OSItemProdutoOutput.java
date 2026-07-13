package br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de resposta de um produto vinculado a uma Ordem de Serviço")
public record OSItemProdutoOutput(
        @Schema(description = "ID do item", example = "1")
        Long id,

        @Schema(description = "ID da Ordem de Serviço", example = "5")
        Long ordemServicoId,

        @Schema(description = "ID do produto", example = "1")
        Long produtoId,

        @Schema(description = "Código do produto", example = "FO-001")
        String codigoProduto,

        @Schema(description = "Nome do produto", example = "Filtro de Óleo")
        String nomeProduto,

        @Schema(description = "Quantidade utilizada", example = "2")
        Integer quantidade,

        @Schema(description = "Preço unitário no momento da inclusão", example = "45.90")
        BigDecimal precoUnitarioNoMomento,

        @Schema(description = "Subtotal (quantidade × preço)", example = "91.80")
        BigDecimal subtotal,

        @Schema(description = "Data de inclusão")
        LocalDateTime dataCriacao
) {
}
