package br.com.autocenterfiap.ordemservico.controller;

import br.com.autocenterfiap.ordemservico.dto.MetricaTempoGastoServicoDTO;
import br.com.autocenterfiap.ordemservico.service.OSItemServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/ordem-servico/metricas")
@RequiredArgsConstructor
@Tag(name = "Metricas de OS/Serviços/Produtos", description = "Endpoints para consulta de metricas em geral")
public class MetricasController {

    private final OSItemServicoService osItemServicoService;

    @Operation(
            summary = "Deve retornar o tempo médio gasto para cada serviço em uma ordem de serviço",
            description = "Retorna o tempo médio gasto para cada serviço em uma ordem de serviço específica"
    )
    @GetMapping("/servicos")
    public ResponseEntity<List<MetricaTempoGastoServicoDTO>> listMediaDeTempoDeCadaServico() {
        List<MetricaTempoGastoServicoDTO> metrica = osItemServicoService.getMetricaTempoGastoServico();
        return ResponseEntity.ok(metrica);
    }

}
