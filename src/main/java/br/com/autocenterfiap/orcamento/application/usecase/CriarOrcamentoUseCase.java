package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.CriarOrcamentoInput;
import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.mapper.OrcamentoApplicationMapper;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;

public class CriarOrcamentoUseCase {

    private final OrcamentoRepositoryPort orcamentoRepositoryPort;

    public CriarOrcamentoUseCase(OrcamentoRepositoryPort orcamentoRepositoryPort) {
        this.orcamentoRepositoryPort = orcamentoRepositoryPort;
    }

    public OrcamentoOutput executar(CriarOrcamentoInput input) {
        Orcamento orcamento = OrcamentoApplicationMapper.toEntity(input);
        orcamento.validarDominio();

        return OrcamentoApplicationMapper.toOutput(this.orcamentoRepositoryPort.salvar(orcamento));
    }
}
