package br.com.autocenterfiap.produto.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.produto.domain.entity.Produto;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.entity.ProdutoJpaEntity;

public class ProdutoJpaMapper {

    public static ProdutoJpaEntity toJpaEntity(Produto domain) {
        if (domain == null) return null;

        return ProdutoJpaEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .codigo(domain.getCodigo())
                .descricao(domain.getDescricao())
                .unidadeMedida(domain.getUnidadeMedida())
                .precoUnitario(domain.getPrecoUnitario())
                .quantidadeEstoque(domain.getQuantidadeEstoque())
                .estoqueMinimo(domain.getEstoqueMinimo())
                .categoria(domain.getCategoria())
                .tipo(domain.getTipo())
                .ativo(domain.getAtivo())
                .dataCriacao(domain.getDataCriacao())
                .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
                .build();
    }

    public static Produto toDomain(ProdutoJpaEntity jpa) {
        if (jpa == null) return null;

        return Produto.builder()
                .id(jpa.getId())
                .nome(jpa.getNome())
                .codigo(jpa.getCodigo())
                .descricao(jpa.getDescricao())
                .unidadeMedida(jpa.getUnidadeMedida())
                .precoUnitario(jpa.getPrecoUnitario())
                .quantidadeEstoque(jpa.getQuantidadeEstoque())
                .estoqueMinimo(jpa.getEstoqueMinimo())
                .categoria(jpa.getCategoria())
                .tipo(jpa.getTipo())
                .ativo(jpa.getAtivo())
                .dataCriacao(jpa.getDataCriacao())
                .dataUltimaAtualizacao(jpa.getDataUltimaAtualizacao())
                .build();
    }
}
