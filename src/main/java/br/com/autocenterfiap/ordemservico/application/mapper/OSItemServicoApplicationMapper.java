package br.com.autocenterfiap.ordemservico.application.mapper;

import br.com.autocenterfiap.ordemservico.adapter.in.dto.MetricaTempoGastoServicoDTO;
import br.com.autocenterfiap.ordemservico.adapter.in.dto.OSItemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.MetricaTempoGastoServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;

public class OSItemServicoApplicationMapper {
    public static OSItemServicoOutput toOutput(OSItemServico osItemServico) {
        if (osItemServico == null) return null;

        return new OSItemServicoOutput(
                osItemServico.getId(),
                osItemServico.getValorItemServico(),
                osItemServico.getStatusServico(),
                osItemServico.getDataHoraInicio(),
                osItemServico.getDataHoraFim()
        );
    }

    public static OSItemServico toEntity(OSItemServicoOutput output) {
        if (output == null) return null;

        return OSItemServico.builder()
                .id(output.id())
                .ordemServico(null)
                .servico(null)
                .valorItemServico(output.valorItemServico())
                .statusServico(output.statusItemServico())
                .dataHoraInicio(output.dataHoraInicio())
                .dataHoraFim(output.dataHoraFim())
                .dataCriacao(null)
                .dataUltimaAtualizacao(null)
                .build();
    }

    public static OSItemServicoResponseDTO osItemServicoOutputToOSItemServicoResponseDTO(
            OSItemServicoOutput osItemServicoOutput
    ) {
        if (osItemServicoOutput == null) return null;

        return new OSItemServicoResponseDTO(
                osItemServicoOutput.id(),
                osItemServicoOutput.valorItemServico(),
                osItemServicoOutput.statusItemServico(),
                osItemServicoOutput.dataHoraInicio(),
                osItemServicoOutput.dataHoraFim()
        );
    }

    public static MetricaTempoGastoServicoDTO toMetricaResponse(MetricaTempoGastoServicoOutput metricaTempoGastoServicoOutput) {
        if (metricaTempoGastoServicoOutput == null) return null;

        return new MetricaTempoGastoServicoDTO(
                metricaTempoGastoServicoOutput.nomeServico(),
                metricaTempoGastoServicoOutput.tempoGastoMinutos()
        );
    }
}
