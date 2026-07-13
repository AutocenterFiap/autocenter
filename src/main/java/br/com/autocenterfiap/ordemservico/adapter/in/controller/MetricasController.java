package br.com.autocenterfiap.ordemservico.adapter.in.controller;

import br.com.autocenterfiap.ordemservico.adapter.in.dto.MetricaTempoGastoServicoDTO;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.MetricaTempoGastoServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemServicoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase.BuscarMetricasTempoGastoServicoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ordem-servico/metricas")
@Tag(name = "Metricas de OS/Serviços/Produtos", description = "Endpoints para consulta de metricas em geral")
public class MetricasController {

    private final BuscarMetricasTempoGastoServicoUseCase buscarMetricasTempoGastoServicoUseCase;

    public MetricasController(BuscarMetricasTempoGastoServicoUseCase buscarMetricasTempoGastoServicoUseCase) {
        this.buscarMetricasTempoGastoServicoUseCase = buscarMetricasTempoGastoServicoUseCase;
    }

    @Operation(
            summary = "Deve retornar o tempo médio gasto para cada serviço em uma ordem de serviço",
            description = "Retorna o tempo médio gasto para cada serviço em uma ordem de serviço específica"
    )
    @GetMapping("/servicos")
    public ResponseEntity<PageResult<MetricaTempoGastoServicoDTO>> listMediaDeTempoDeCadaServico(
            Pageable pageable
    ) {
        PaginationRequest pagination = new PaginationRequest(pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(
            this.buscarMetricasTempoGastoServicoUseCase.executar(pagination)
                .map(OSItemServicoApplicationMapper::toMetricaResponse)
        );
    }
}
