package br.com.autocenterfiap.servico.adapter.in.dto;

import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de resposta do serviço cadastrado")
public class ServicoResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador único do serviço", example = "1")
    private Long id;

    @Schema(description = "Descrição do serviço", example = "Alinhamento e Balanceamento")
    private String descricao;

    @Schema(description = "Status do serviço", example = "ATIVO")
    private StatusServico status;

    @Schema(description = "Valor do serviço", example = "150.00")
    private BigDecimal valor;
}
