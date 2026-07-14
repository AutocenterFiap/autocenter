package br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.entity.OrcamentoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;

public class OrcamentoJpaMapper {

    public static Orcamento toDomain(OrcamentoJpaEntity entity) {

        if (entity == null) return null;

        return Orcamento.builder()
                .id(entity.getId())
                .ordemServicoId(entity.getOrdemServicoJpaEntity().getId())
                .valorTotal(entity.getValorTotal())
                .statusOrcamento(entity.getStatusOrcamento())
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();
    }

    public static OrcamentoJpaEntity toJpa(Orcamento domain, OrdemServicoJpaEntity ordemServicoJpaEntity) {

        if (domain == null) return null;

        return OrcamentoJpaEntity.builder()
                .id(domain.getId())
                .ordemServicoJpaEntity(ordemServicoJpaEntity)
                .valorTotal(domain.getValorTotal())
                .statusOrcamento(domain.getStatusOrcamento())
                .dataCriacao(domain.getDataCriacao())
                .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
                .build();


    }

}
