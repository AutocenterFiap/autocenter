package br.com.autocenterfiap.servico.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.entity.ServicoJpaEntity;

public class ServicoJpaMapper {

    public static Servico toDomain(ServicoJpaEntity entity) {
        if (entity == null) return null;

        return Servico.builder()
                .id(entity.getId())
                .descricao(entity.getDescricao())
                .status(entity.getStatus())
                .valor(entity.getValor())
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();
    }

    public static ServicoJpaEntity toJpa(Servico domain) {
        if (domain == null) return null;

        return ServicoJpaEntity.builder()
                .id(domain.getId())
                .descricao(domain.getDescricao())
                .status(domain.getStatus())
                .valor(domain.getValor())
                .dataCriacao(domain.getDataCriacao())
                .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
                .build();
    }
}
