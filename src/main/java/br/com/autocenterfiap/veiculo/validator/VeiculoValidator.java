package br.com.autocenterfiap.veiculo.validator;

import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;

public interface VeiculoValidator {
    void validate(VeiculoValidationContext context);
}
