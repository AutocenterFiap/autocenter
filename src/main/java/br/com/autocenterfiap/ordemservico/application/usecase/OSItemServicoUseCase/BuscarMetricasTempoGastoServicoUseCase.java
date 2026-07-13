package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.MetricaTempoGastoServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BuscarMetricasTempoGastoServicoUseCase {

    private final OSItemServicoRepositoryPort itemServicoRepositoryPort;

    public BuscarMetricasTempoGastoServicoUseCase(OSItemServicoRepositoryPort itemServicoRepositoryPort) {
        this.itemServicoRepositoryPort = itemServicoRepositoryPort;
    }

    public PageResult<MetricaTempoGastoServicoOutput> executar(PaginationRequest pagination) {
        log.info("Calculando métrica de tempo gasto por serviço");

        List<MetricaTempoGastoServicoOutput> resultado = this.itemServicoRepositoryPort.findAllFinalizados(pagination).getContent()
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getServico().getDescricao(),
                        Collectors.averagingLong(item ->
                                Duration.between(item.getDataHoraInicio(), item.getDataHoraFim()).getSeconds())
                ))
                .entrySet().stream()
                .map(e -> new MetricaTempoGastoServicoOutput(e.getKey(), e.getValue().toString()))
                .toList();

        return new PageResult<>(resultado, 0, resultado.size(), resultado.size(), 1);
    }
}
