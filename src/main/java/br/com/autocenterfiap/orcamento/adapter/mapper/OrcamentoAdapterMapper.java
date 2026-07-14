package br.com.autocenterfiap.orcamento.adapter.mapper;

import br.com.autocenterfiap.orcamento.adapter.in.dto.OrcamentoResponse;
import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;

public class OrcamentoAdapterMapper {

    public static OrcamentoResponse orcamentoOutputToOrcamentoResponse(OrcamentoOutput orcamentoResponse) {
        if (orcamentoResponse == null) return null;

        return OrcamentoResponse.builder()
                .id(orcamentoResponse.getId())
                .ordemServicoId(orcamentoResponse.getOrdemServicoId())
                .valorTotal(orcamentoResponse.getValorTotal())
                .statusOrcamento(orcamentoResponse.getStatusOrcamento())
                .dataCriacao(orcamentoResponse.getDataCriacao())
                .dataUltimaAtualizacao(orcamentoResponse.getDataUltimaAtualizacao())
                .build();
    }

}
