package br.com.autocenterfiap.veiculo.application.usecase;

import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;
import br.com.autocenterfiap.veiculo.application.mapper.VeiculoApplicationMapper;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;

public class BuscarVeiculoPorIdUseCase {

    private final VeiculoRepositoryPort veiculoRepositoryPort;

    public BuscarVeiculoPorIdUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        this.veiculoRepositoryPort = veiculoRepositoryPort;
    }

    public VeiculoOutput executar(Long id) {
        Veiculo veiculo = veiculoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new VeiculoNaoEncontradoException("Veiculo com ID " + id + " não existe!"));
        return VeiculoApplicationMapper.toOutput(veiculo);
    }
}
