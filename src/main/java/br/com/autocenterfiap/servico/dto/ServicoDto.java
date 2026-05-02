package br.com.autocenterfiap.servico.dto;

import br.com.autocenterfiap.servico.enums.StatusServico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServicoDto(
    @NotBlank(message = "Descricao é obrigatório")
    String descricao,
    @NotNull(message = "Status é obrigatório")
    StatusServico status,
    @NotNull(message = "Valor é obrigatório")
    BigDecimal valor
) {}
