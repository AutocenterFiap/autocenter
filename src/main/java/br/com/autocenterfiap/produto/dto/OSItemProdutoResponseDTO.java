package br.com.autocenterfiap.produto.dto;

import br.com.autocenterfiap.ordemservico.repository.entity.OSItemProduto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de resposta de um produto vinculado a uma Ordem de Serviço")
public record OSItemProdutoResponseDTO(

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
    public static OSItemProdutoResponseDTO from(OSItemProduto item) {
        return new OSItemProdutoResponseDTO(
                item.getId(),
                item.getOrdemServico().getId(),
                item.getProduto().getId(),
                item.getProduto().getCodigo(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitarioNoMomento(),
                item.calcularSubtotal(),
                item.getDataCriacao()
        );
    }
}
