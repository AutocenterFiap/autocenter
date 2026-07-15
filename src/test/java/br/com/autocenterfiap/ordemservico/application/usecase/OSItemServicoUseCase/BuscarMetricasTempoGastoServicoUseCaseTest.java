package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.MetricaTempoGastoServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarMetricasTempoGastoServicoUseCase - Testes Unitários")
class BuscarMetricasTempoGastoServicoUseCaseTest {

    @Mock
    private OSItemServicoRepositoryPort itemServicoRepositoryPort;

    @InjectMocks
    private BuscarMetricasTempoGastoServicoUseCase useCase;

    private OSItemServico itemFinalizado(String descricao, long duracaoSegundos) {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        return OSItemServico.builder()
                .servico(Servico.builder().descricao(descricao).build())
                .dataHoraInicio(inicio)
                .dataHoraFim(inicio.plusSeconds(duracaoSegundos))
                .build();
    }

    @Test
    @DisplayName("Deve calcular a média de tempo gasto agrupada por serviço")
    void deveCalcularMediaTempoGasto() {
        PaginationRequest pagination = new PaginationRequest(0, 10);
        List<OSItemServico> itens = List.of(
                itemFinalizado("Troca de óleo", 60),
                itemFinalizado("Troca de óleo", 120));
        PageResult<OSItemServico> page = new PageResult<>(itens, 0, 10, 2, 1);

        when(itemServicoRepositoryPort.findAllFinalizados(any(PaginationRequest.class))).thenReturn(page);

        PageResult<MetricaTempoGastoServicoOutput> resultado = useCase.executar(pagination);

        assertEquals(1, resultado.getContent().size());
        MetricaTempoGastoServicoOutput metrica = resultado.getContent().get(0);
        assertEquals("Troca de óleo", metrica.nomeServico());
        assertEquals("90.0", metrica.tempoGastoMinutos());
    }

    @Test
    @DisplayName("Deve retornar resultado vazio quando não houver serviços finalizados")
    void deveRetornarVazioQuandoSemFinalizados() {
        PaginationRequest pagination = new PaginationRequest(0, 10);
        PageResult<OSItemServico> page = new PageResult<>(Collections.emptyList(), 0, 10, 0, 0);

        when(itemServicoRepositoryPort.findAllFinalizados(any(PaginationRequest.class))).thenReturn(page);

        PageResult<MetricaTempoGastoServicoOutput> resultado = useCase.executar(pagination);

        assertTrue(resultado.getContent().isEmpty());
    }
}
