package br.com.autocenterfiap.ordemservico.infrastructure.observabilidade;

import br.com.autocenterfiap.ordemservico.application.port.ObservabilidadePort;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MicrometerObservabilidadeAdapter implements ObservabilidadePort {

    private final MeterRegistry meterRegistry;

    public MicrometerObservabilidadeAdapter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMetrics() {
        this.meterRegistry.counter("autocenter.erros.total", "integracao", "ordem_servico", "erro", "nenhum");
    }

    @Override
    public void registrarOrdemCriada() {
        this.meterRegistry.counter("autocenter.ordem_servico.criada.total").increment();
    }

    @Override
    public void registrarTempoStatus(StatusOS status, double segundos) {
        this.meterRegistry.timer("autocenter.ordem_servico.tempo_status.seconds", "status", status.name())
                .record((long) (segundos * 1000), TimeUnit.MILLISECONDS);
    }

    @Override
    public void registrarErroIntegracao(String integracao, String erro) {
        this.meterRegistry.counter("autocenter.erros.total", "integracao", integracao, "erro", erro).increment();
    }
}
