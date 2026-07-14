package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.exception.OrcamentoNaoEncontradoException;
import br.com.autocenterfiap.orcamento.application.mapper.OrcamentoApplicationMapper;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;

public class BuscarOrcamentoPorIdUseCase {

    private final OrcamentoRepositoryPort orcamentoRepositoryPort;

    public BuscarOrcamentoPorIdUseCase(OrcamentoRepositoryPort orcamentoRepositoryPort) {
        this.orcamentoRepositoryPort = orcamentoRepositoryPort;
    }

    public OrcamentoOutput executar(Long id) {
        Orcamento orcamento = this.orcamentoRepositoryPort.buscarPorId(id).orElseThrow(
                () -> new OrcamentoNaoEncontradoException("Orçamento não encontrado para o id: " + id)
        );

        return OrcamentoApplicationMapper.toOutput(orcamento);
    }

}
