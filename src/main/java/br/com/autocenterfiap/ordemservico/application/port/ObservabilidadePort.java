package br.com.autocenterfiap.ordemservico.application.port;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;

public interface ObservabilidadePort {
    void registrarOrdemCriada();
    void registrarTempoStatus(StatusOS status, double segundos);
    void registrarErroIntegracao(String integracao, String erro);
}
