package br.com.autocenterfiap.peca.dto;

import br.com.autocenterfiap.peca.model.OSItemPeca;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de resposta de uma peça vinculada a uma Ordem de Serviço")
public record OSItemPecaResponseDTO(

        @Schema(description = "ID do item", example = "1")
        Long id,

        @Schema(description = "ID da Ordem de Serviço", example = "5")
        Long ordemServicoId,

        @Schema(description = "ID da peça", example = "1")
        Long pecaId,

        @Schema(description = "Código da peça", example = "FO-001")
        String codigoPeca,

        @Schema(description = "Nome da peça", example = "Filtro de Óleo")
        String nomePeca,

        @Schema(description = "Quantidade utilizada", example = "2")
        Integer quantidade,

        @Schema(description = "Preço unitário no momento da inclusão", example = "45.90")
        BigDecimal precoUnitarioNoMomento,

        @Schema(description = "Subtotal (quantidade × preço)", example = "91.80")
        BigDecimal subtotal,

        @Schema(description = "Data de inclusão")
        LocalDateTime dataCriacao
) {
    public static OSItemPecaResponseDTO from(OSItemPeca item) {
        return new OSItemPecaResponseDTO(
                item.getId(),
                item.getOrdemServicoId(),
                item.getPeca().getId(),
                item.getPeca().getCodigo(),
                item.getPeca().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitarioNoMomento(),
                item.calcularSubtotal(),
                item.getDataCriacao()
        );
    }
}
