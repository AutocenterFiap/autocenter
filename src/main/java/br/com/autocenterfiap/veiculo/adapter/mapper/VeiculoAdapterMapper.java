package br.com.autocenterfiap.veiculo.adapter.mapper;

import br.com.autocenterfiap.veiculo.adapter.in.dto.VeiculoRequestDTO;
import br.com.autocenterfiap.veiculo.adapter.in.dto.VeiculoResponseDTO;
import br.com.autocenterfiap.veiculo.application.dto.AtualizarVeiculoInput;
import br.com.autocenterfiap.veiculo.application.dto.CriarVeiculoInput;
import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;

public class VeiculoAdapterMapper {

    public static CriarVeiculoInput criarVeiculoRequestToCriarVeiculoInput(VeiculoRequestDTO request) {
        if (request == null) return null;

        return CriarVeiculoInput.builder()
            .placa(request.getPlaca())
            .chassi(request.getChassi())
            .renavam(request.getRenavam())
            .marca(request.getMarca())
            .modelo(request.getModelo())
            .anoFabricacao(request.getAnoFabricacao())
            .anoModelo(request.getAnoModelo())
            .cor(request.getCor())
            .quilometragem(request.getQuilometragem())
            .tipoCombustivel(request.getTipoCombustivel())
            .categoriaVeiculo(request.getCategoriaVeiculo())
            .build();
    }

    public static AtualizarVeiculoInput atualizarVeiculoRequestToAtualizarVeiculoInput(VeiculoRequestDTO request) {
        if (request == null) return null;

        return AtualizarVeiculoInput.builder()
            .placa(request.getPlaca())
            .chassi(request.getChassi())
            .renavam(request.getRenavam())
            .marca(request.getMarca())
            .modelo(request.getModelo())
            .anoFabricacao(request.getAnoFabricacao())
            .anoModelo(request.getAnoModelo())
            .cor(request.getCor())
            .quilometragem(request.getQuilometragem())
            .tipoCombustivel(request.getTipoCombustivel())
            .categoriaVeiculo(request.getCategoriaVeiculo())
            .build();
    }

    public static VeiculoResponseDTO veiculoOutputToVeiculoResponse(VeiculoOutput output) {
        if (output == null) return null;

        return VeiculoResponseDTO.builder()
            .id(output.getId())
            .placa(output.getPlaca())
            .chassi(output.getChassi())
            .renavam(output.getRenavam())
            .marca(output.getMarca())
            .modelo(output.getModelo())
            .anoFabricacao(output.getAnoFabricacao())
            .anoModelo(output.getAnoModelo())
            .cor(output.getCor())
            .quilometragem(output.getQuilometragem())
            .tipoCombustivel(output.getTipoCombustivel())
            .categoriaVeiculo(output.getCategoriaVeiculo())
            .dataCadastro(output.getDataCadastro())
            .dataUltimaAtualizacao(output.getDataUltimaAtualizacao())
            .build();
    }
}
