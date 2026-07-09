package br.com.autocenterfiap.veiculo.application.mapper;

import br.com.autocenterfiap.veiculo.application.dto.CriarVeiculoInput;
import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;

public class VeiculoApplicationMapper {

    public static Veiculo toEntity(CriarVeiculoInput input) {
        if (input == null) return null;

        return Veiculo.builder()
            .placa(input.getPlaca())
            .chassi(input.getChassi())
            .renavam(input.getRenavam())
            .marca(input.getMarca())
            .modelo(input.getModelo())
            .anoFabricacao(input.getAnoFabricacao())
            .anoModelo(input.getAnoModelo())
            .cor(input.getCor())
            .quilometragem(input.getQuilometragem())
            .tipoCombustivel(input.getTipoCombustivel())
            .categoriaVeiculo(input.getCategoriaVeiculo())
            .build();
    }

    public static VeiculoOutput toOutput(Veiculo entity) {
        if (entity == null) return null;

        return VeiculoOutput.builder()
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
}
