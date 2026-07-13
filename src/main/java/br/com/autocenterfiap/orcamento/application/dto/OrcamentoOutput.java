package br.com.autocenterfiap.orcamento.application.dto;

import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrcamentoOutput {

    private Long id;
    private Long ordemServicoId;
    private BigDecimal valorTotal;
    private StatusOrcamento statusOrcamento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
}
