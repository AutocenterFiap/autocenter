package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.mapper.ClienteJpaMapper;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.mapper.VeiculoJpaMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class OrdemServicoJpaMapper {

    public static OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        if (entity == null) return null;

        return OrdemServico.builder()
                .id(entity.getId())
                .numeroOrdemServico(entity.getNumeroOrdemServico())
                .statusOS(entity.getStatusOS())
                .valorTotal(entity.getValorTotal())
                .veiculo(VeiculoJpaMapper.toDomain(entity.getVeiculo()))
                .cliente(ClienteJpaMapper.toDomain(entity.getCliente()))
                .osItensServicos(entity.getOsItensServicos() != null
                        ? entity.getOsItensServicos().stream().map(OSItemServicoJpaMapper::toDomain).collect(Collectors.toCollection(ArrayList::new))
                        : new ArrayList<>())
                .osItensProdutos(entity.getOsItensProdutos() != null
                        ? entity.getOsItensProdutos().stream().map(OSItemProdutoJpaMapper::toDomain).collect(Collectors.toCollection(ArrayList::new))
                        : new ArrayList<>())
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();
    }

    public static OrdemServico toDomainShallow(OrdemServicoJpaEntity entity) {
        if (entity == null) return null;

        return OrdemServico.builder()
                .id(entity.getId())
                .numeroOrdemServico(entity.getNumeroOrdemServico())
                .statusOS(entity.getStatusOS())
                .valorTotal(entity.getValorTotal())
                .veiculo(VeiculoJpaMapper.toDomain(entity.getVeiculo()))
                .cliente(ClienteJpaMapper.toDomain(entity.getCliente()))
                .dataCriacao(entity.getDataCriacao())
                .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
                .build();
    }

    /**
     * Maps OS without item collections. Used when setting parent reference in child mappers
     * to prevent circular recursion (OS → items → OS → ...).
     */
    public static OrdemServicoJpaEntity toJpaShallow(OrdemServico domain) {
        if (domain == null) return null;

        return OrdemServicoJpaEntity.builder()
                .id(domain.getId())
                .numeroOrdemServico(domain.getNumeroOrdemServico())
                .statusOS(domain.getStatusOS())
                .valorTotal(domain.getValorTotal())
                .veiculo(VeiculoJpaMapper.toJpa(domain.getVeiculo()))
                .cliente(ClienteJpaMapper.toJpa(domain.getCliente()))
                .dataCriacao(domain.getDataCriacao())
                .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
                .build();
    }

    public static OrdemServicoJpaEntity toJpa(OrdemServico domain) {
        if (domain == null) return null;

        OrdemServicoJpaEntity entity = toJpaShallow(domain);

        if (domain.getOsItensServicos() != null) {
            entity.setOsItensServicos(domain.getOsItensServicos().stream()
                    .map(OSItemServicoJpaMapper::toJpa)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        if (domain.getOsItensProdutos() != null) {
            entity.setOsItensProdutos(domain.getOsItensProdutos().stream()
                    .map(OSItemProdutoJpaMapper::toJpa)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        return entity;
    }

}
