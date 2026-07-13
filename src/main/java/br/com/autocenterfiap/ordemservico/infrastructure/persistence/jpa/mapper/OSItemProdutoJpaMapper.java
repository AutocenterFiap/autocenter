package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemProdutoJpaEntity;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.mapper.ProdutoJpaMapper;

public class OSItemProdutoJpaMapper {

    public static OSItemProduto toDomain(OSItemProdutoJpaEntity entity) {
        if (entity == null) return null;

        return OSItemProduto
                .builder()
                .id(entity.getId())
                .ordemServico(OrdemServicoJpaMapper.toDomainShallow(entity.getOrdemServicoJpaEntity()))
                .produto(ProdutoJpaMapper.toDomain(entity.getProduto()))
                .quantidade(entity.getQuantidade())
                .precoUnitarioNoMomento(entity.getPrecoUnitarioNoMomento())
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();
    }

    public static OSItemProdutoJpaEntity toJpa(OSItemProduto domain) {
        if (domain == null) return null;

        return OSItemProdutoJpaEntity
                .builder()
                .id(domain.getId())
                .ordemServicoJpaEntity(OrdemServicoJpaMapper.toJpaShallow(domain.getOrdemServico()))
                .produto(ProdutoJpaMapper.toJpaEntity(domain.getProduto()))
                .quantidade(domain.getQuantidade())
                .precoUnitarioNoMomento(domain.getPrecoUnitarioNoMomento())
                .dataCriacao(domain.getDataCriacao())
                .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
                .build();


    }

}
