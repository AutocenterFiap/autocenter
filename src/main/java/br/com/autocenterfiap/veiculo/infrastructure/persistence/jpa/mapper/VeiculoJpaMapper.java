package br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.mapper;

import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.entity.VeiculoJpaEntity;

public class VeiculoJpaMapper {

    public static Veiculo toDomain(VeiculoJpaEntity entity) {
        if (entity == null) return null;

        return Veiculo.builder()
            .id(entity.getId())
            .placa(entity.getPlaca())
            .chassi(entity.getChassi())
            .renavam(entity.getRenavam())
            .marca(entity.getMarca())
            .modelo(entity.getModelo())
            .anoFabricacao(entity.getAnoFabricacao())
            .anoModelo(entity.getAnoModelo())
            .cor(entity.getCor())
            .quilometragem(entity.getQuilometragem())
            .tipoCombustivel(entity.getTipoCombustivel())
            .categoriaVeiculo(entity.getCategoriaVeiculo())
            .dataCadastro(entity.getDataCadastro())
            .dataUltimaAtualizacao(entity.getDataUltimaAtualizacao())
            .build();
    }

    public static VeiculoJpaEntity toJpa(Veiculo domain) {
        if (domain == null) return null;

        return VeiculoJpaEntity.builder()
            .id(domain.getId())
            .placa(domain.getPlaca())
            .chassi(domain.getChassi())
            .renavam(domain.getRenavam())
            .marca(domain.getMarca())
            .modelo(domain.getModelo())
            .anoFabricacao(domain.getAnoFabricacao())
            .anoModelo(domain.getAnoModelo())
            .cor(domain.getCor())
            .quilometragem(domain.getQuilometragem())
            .tipoCombustivel(domain.getTipoCombustivel())
            .categoriaVeiculo(domain.getCategoriaVeiculo())
            .dataCadastro(domain.getDataCadastro())
            .dataUltimaAtualizacao(domain.getDataUltimaAtualizacao())
            .build();
    }
}
