package br.com.autocenterfiap.servico.application.dto;

import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoOutput {
    private Long id;
    private String descricao;
    private StatusServico status;
    private BigDecimal valor;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
}
