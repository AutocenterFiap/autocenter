package br.com.autocenterfiap.scheduler;

import br.com.autocenterfiap.orcamento.service.OrcamentoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoScheduler {

    private final OrcamentoService orcamentoService;

    public OrcamentoScheduler(OrcamentoService orcamentoService) {
        this.orcamentoService = orcamentoService;
    }

    @Scheduled(fixedDelayString = "${orcamento.scheduler.fixedDelay}")
    public void executar() {
        orcamentoService.gerarOrcamento();
    }
}
