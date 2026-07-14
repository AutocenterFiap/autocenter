package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemServicoJpaEntity;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.mapper.ServicoJpaMapper;

public class OSItemServicoJpaMapper {

    public static OSItemServico toDomain(OSItemServicoJpaEntity entity) {
        if (entity == null) return null;

        return OSItemServico.builder()
                .id(entity.getId())
                .ordemServico(OrdemServicoJpaMapper.toDomainShallow(entity.getOrdemServicoJpaEntity()))
                .servico(ServicoJpaMapper.toDomain(entity.getServico()))
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
                .ordemServicoJpaEntity(OrdemServicoJpaMapper.toJpaShallow(domain.getOrdemServico()))
                .servico(ServicoJpaMapper.toJpa(domain.getServico()))
                .valorItemServico(domain.getValorItemServico())
                .statusServico(domain.getStatusServico())
                .dataHoraInicio(domain.getDataHoraInicio())
                .dataHoraFim(domain.getDataHoraFim())
                .dataCriacao(domain.getDataCriacao())
                .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
                .build();


    }

}
