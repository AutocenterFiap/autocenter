package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.cliente.adapter.mapper.ClienteAdapterMapper;
import br.com.autocenterfiap.cliente.application.mapper.ClienteApplicationMapper;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.mapper.VeiculoJpaMapper;

public class OrdemServicoJpaMapper {

    public static OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        if (entity == null) return null;

        return OrdemServico.builder()
                .id(entity.getId())
                .numeroOrdemServico(entity.getNumeroOrdemServico())
                .statusOS(entity.getStatusOS())
                .valorTotal(entity.getValorTotal())
                .veiculo(VeiculoJpaMapper.toDomain(entity.getVeiculo()))
                .cliente(entity.getCliente())
                .osItensServicos(entity.getOsItensServicos().stream().map(OSItemServicoJpaMapper::toDomain).toList())
                .osItensProdutos(entity.getOsItensProdutos().stream().map(OSItemProdutoJpaMapper::toDomain).toList())
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();
    }

    public static OrdemServicoJpaEntity toJpa(OrdemServico domain) {
        if (domain == null) return null;

        return OrdemServicoJpaEntity.builder()
                .id(domain.getId())
                .numeroOrdemServico(domain.getNumeroOrdemServico())
                .statusOS(domain.getStatusOS())
                .valorTotal(domain.getValorTotal())
                .veiculo(VeiculoJpaMapper.toJpa(domain.getVeiculo()))
                .cliente(domain.getCliente())
                .osItensServicos(domain.getOsItensServicos().stream().map(OSItemServicoJpaMapper::toJpa).toList())
                .osItensProdutos(domain.getOsItensProdutos().stream().map(OSItemProdutoJpaMapper::toJpa).toList())
                .dataCriacao(domain.getDataCriacao())
                .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
                .build();
    }

}
