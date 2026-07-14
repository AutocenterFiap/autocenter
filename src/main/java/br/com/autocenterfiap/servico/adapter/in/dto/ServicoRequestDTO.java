package br.com.autocenterfiap.servico.adapter.in.dto;

import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Dados de entrada para cadastro/atualização de serviço")
public class ServicoRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Descricao é obrigatório")
    @Schema(description = "Descrição do serviço", example = "Alinhamento e Balanceamento", requiredMode = Schema.RequiredMode.REQUIRED)
    private String descricao;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status do serviço", example = "ATIVO", requiredMode = Schema.RequiredMode.REQUIRED)
    private StatusServico status;

    @NotNull(message = "Valor é obrigatório")
    @Schema(description = "Valor do serviço", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal valor;
}
