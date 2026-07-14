package br.com.autocenterfiap.orcamento.scheduler;

import br.com.autocenterfiap.orcamento.application.usecase.GerarOrcamentoUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoScheduler {

    private final GerarOrcamentoUseCase gerarOrcamentoUseCase;

    public OrcamentoScheduler(GerarOrcamentoUseCase gerarOrcamentoUseCase) {
        this.gerarOrcamentoUseCase = gerarOrcamentoUseCase;
    }

    @Scheduled(fixedDelayString = "${orcamento.scheduler.fixedDelay}")
    public void executar() {

        this.gerarOrcamentoUseCase.executar();
    }
}
