package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.exception.OrcamentoNaoEncontradoException;
import br.com.autocenterfiap.orcamento.application.mapper.OrcamentoApplicationMapper;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;

public class ReprovarOrcamentoUseCase {

    private final OrcamentoRepositoryPort orcamentoRepositoryPort;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public ReprovarOrcamentoUseCase(OrcamentoRepositoryPort orcamentoRepositoryPort, OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        this.orcamentoRepositoryPort = orcamentoRepositoryPort;
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
    }

    public OrcamentoOutput executar(Long id) {
        Orcamento orcamento = this.orcamentoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(id));

        orcamento.reprovar();
        this.cancelarOrdemServico(orcamento.getOrdemServicoId());

        Orcamento orcamentoSalvo = this.orcamentoRepositoryPort.salvar(orcamento);
        return OrcamentoApplicationMapper.toOutput(orcamentoSalvo);

    }

    private void cancelarOrdemServico(Long ordemServicoId) {
        this.ordemServicoRepositoryPort.findById(ordemServicoId).ifPresent(OrdemServico::cancelar);
    }
}
