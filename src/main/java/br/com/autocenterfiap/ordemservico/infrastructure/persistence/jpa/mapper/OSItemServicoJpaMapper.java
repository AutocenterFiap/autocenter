package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemServicoJpaEntity;

public class OSItemServicoJpaMapper {

    public static OSItemServico toDomain(OSItemServicoJpaEntity entity) {
        if (entity == null) return null;

        return OSItemServico.builder()
                .id(entity.getId())
                .ordemServico(OrdemServicoJpaMapper.toDomain(entity.getOrdemServicoJpaEntity()))
                .servico(entity.getServico())
                .valorItemServico(entity.getValorItemServico())
                .statusServico(entity.getStatusServico())
                .dataHoraInicio(entity.getDataHoraInicio())
                .dataHoraFim(entity.getDataHoraFim())
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();
    }

    public static OSItemServicoJpaEntity toJpa(OSItemServico domain) {
        if (domain == null) return null;

        return OSItemServicoJpaEntity.builder()
                .id(domain.getId())
                .ordemServicoJpaEntity(OrdemServicoJpaMapper.toJpa(domain.getOrdemServico()))
                .servico(domain.getServico())
                .valorItemServico(domain.getValorItemServico())
                .statusServico(domain.getStatusServico())
                .dataHoraInicio(domain.getDataHoraInicio())
                .dataHoraFim(domain.getDataHoraFim())
                .dataCriacao(domain.getDataCriacao())
                .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
                .build();


    }

}
