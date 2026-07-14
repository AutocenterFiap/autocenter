package br.com.autocenterfiap.ordemservico.application.validator;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;

public interface OrdemServicoValidator {
    void validate(CriarOrdemServicoInput dto);
}
