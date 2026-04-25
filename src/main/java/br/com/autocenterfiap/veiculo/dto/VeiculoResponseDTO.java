package br.com.autocenterfiap.veiculo.dto;

import br.com.autocenterfiap.veiculo.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.model.Veiculo;

public record VeiculoResponseDTO(Long id,
                                 String placa,
                                 String chassi,
                                 String renavam,
                                 String marca,
                                 String modelo,
                                 Integer anoFabricacao,
                                 Integer anoModelo,
                                 String cor,
                                 Long quilometragem,
                                 TipoCombustivel tipoCombustivel,
                                 CategoriaVeiculo categoriaVeiculo) {

    public VeiculoResponseDTO(Veiculo veiculo){
        this(
            veiculo.getId(),
            veiculo.getPlaca(),
            veiculo.getChassi(),
            veiculo.getRenavam(),
            veiculo.getMarca(),
            veiculo.getModelo(),
            veiculo.getAnoFabricacao(),
            veiculo.getAnoModelo(),
            veiculo.getCor(),
            veiculo.getQuilometragem(),
            veiculo.getTipoCombustivel(),
            veiculo.getCategoriaVeiculo()
        );
    }
}
