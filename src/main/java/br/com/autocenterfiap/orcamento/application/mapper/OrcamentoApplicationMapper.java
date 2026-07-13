package br.com.autocenterfiap.orcamento.application.mapper;

import br.com.autocenterfiap.orcamento.application.dto.CriarOrcamentoInput;
import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;

public class OrcamentoApplicationMapper {

    public static Orcamento toEntity(CriarOrcamentoInput input) {
        if (input == null) return null;

        return Orcamento.builder()
                .id(input.getId())
                .ordemServicoId(input.getOrdemServicoId())
                .valorTotal(input.getValorTotal())
                .statusOrcamento(input.getStatusOrcamento())
                .dataCriacao(input.getDataCriacao())
                .dataUltimaAtualizacao(input.getDataUltimaAtualizacao())
                .build();
    }

    public static OrcamentoOutput toOutput(Orcamento entity) {
        if (entity == null) return null;

        return OrcamentoOutput.builder()
                .id(entity.getId())
                .ordemServicoId(entity.getOrdemServicoId())
                .valorTotal(entity.getValorTotal())
                .statusOrcamento(entity.getStatusOrcamento())
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();


    }
}
