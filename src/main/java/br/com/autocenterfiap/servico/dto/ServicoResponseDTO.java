package br.com.autocenterfiap.servico.dto;

import br.com.autocenterfiap.servico.enums.StatusServico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Dados de resposta do cliente cadastrado")
public record ServicoResponseDTO(
        @NotNull
        Long id,
        @NotBlank(message = "Descricao é obrigatório")
        String descricao,
        @NotBlank
        StatusServico status,
        @NotBlank(message = "Status é obrigatório")
        BigDecimal valor
) {
}
