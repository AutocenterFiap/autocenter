package br.com.autocenterfiap.orcamento.scheduler;

import br.com.autocenterfiap.orcamento.application.usecase.GerarOrcamentoUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrcamentoScheduler {

    private final GerarOrcamentoUseCase gerarOrcamentoUseCase;

    public OrcamentoScheduler(GerarOrcamentoUseCase gerarOrcamentoUseCase) {
        this.gerarOrcamentoUseCase = gerarOrcamentoUseCase;
    }

    @Scheduled(fixedDelayString = "${orcamento.scheduler.fixedDelay}")
    public void executar() {
        try {
            this.gerarOrcamentoUseCase.executar();
        } catch (Exception e) {
            log.error("Erro ao executar agendador de orçamentos: {}", e.getMessage(), e);
        }
    }
}
