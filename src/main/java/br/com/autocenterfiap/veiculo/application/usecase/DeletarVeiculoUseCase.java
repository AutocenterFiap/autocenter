package br.com.autocenterfiap.veiculo.application.usecase;

import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoEmUsoException;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;

public class DeletarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepositoryPort;

    public DeletarVeiculoUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        this.veiculoRepositoryPort = veiculoRepositoryPort;
    }

    public void executar(Long id) {
        // Verificar se existe
        veiculoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new VeiculoNaoEncontradoException("Veiculo com ID " + id + " não existe!"));

        // Verificar se está associado a alguma ordem de serviço
        boolean veiculoEmUso = veiculoRepositoryPort.existeOrdemServicoAssociada(id);
        if (veiculoEmUso) {
            throw new VeiculoEmUsoException("Não é possível deletar o veículo, pois ele está associado a uma ordem de serviço ativa.");
        }

        veiculoRepositoryPort.deletarPorId(id);
    }
}
