package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.CriarOrcamentoInput;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;

public class GerarOrcamentoUseCase {

    private final OrcamentoRepositoryPort orcamentoRepositoryPort;
    private final CriarOrcamentoUseCase criarOrcamentoUseCase;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public GerarOrcamentoUseCase(OrcamentoRepositoryPort orcamentoRepositoryPort, CriarOrcamentoUseCase criarOrcamentoUseCase, OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        this.orcamentoRepositoryPort = orcamentoRepositoryPort;
        this.criarOrcamentoUseCase = criarOrcamentoUseCase;
        this.ordemServicoRepositoryPort = ordemServicoRepositoryPort;
    }

    public void executar() {
        PaginationRequest pagination = new PaginationRequest(0, 100);
        PageResult<OrdemServico> ordens = this.ordemServicoRepositoryPort.findByStatus(StatusOS.AGUARDANDO_APROVACAO, pagination);

        ordens.getContent().forEach(os -> {
            if (this.orcamentoRepositoryPort.buscarOrcamentoAguardandoAprovacaoPorOS(os.getId()).isEmpty()) {
                CriarOrcamentoInput input = CriarOrcamentoInput.builder()
                        .ordemServicoId(os.getId())
                        .valorTotal(os.getValorTotal())
                        .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                        .build();
                this.criarOrcamentoUseCase.executar(input);
            }
        });
    }

}
