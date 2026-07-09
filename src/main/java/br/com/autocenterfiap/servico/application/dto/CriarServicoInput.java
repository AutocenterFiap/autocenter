package br.com.autocenterfiap.servico.application.dto;

import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriarServicoInput {
    private String descricao;
    private StatusServico status;
    private BigDecimal valor;
}
