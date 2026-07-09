package br.com.autocenterfiap.veiculo.application.usecase;

import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;
import br.com.autocenterfiap.veiculo.application.mapper.VeiculoApplicationMapper;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;

public class BuscarVeiculoPorPlacaUseCase {

    private final VeiculoRepositoryPort veiculoRepositoryPort;

    public BuscarVeiculoPorPlacaUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        this.veiculoRepositoryPort = veiculoRepositoryPort;
    }

    public VeiculoOutput executar(String placa) {
        Veiculo veiculo = veiculoRepositoryPort.buscarPorPlaca(placa)
            .orElseThrow(() -> new VeiculoNaoEncontradoException("Veiculo com Placa " + placa + " não existe!"));
        return VeiculoApplicationMapper.toOutput(veiculo);
    }
}
