package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.exception.ServicoEmUsoException;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;

public class DeletarServicoUseCase {

    private final ServicoRepositoryPort repositoryPort;

    public DeletarServicoUseCase(ServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public void executar(Long id) {
        repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException("Servico não encontrado de ID " + id));

        if (repositoryPort.existeOrdemServicoAssociada(id)) {
            throw new ServicoEmUsoException("Não é possível deletar o serviço, pois ele está associado a uma ordem de serviço.");
        }

        repositoryPort.deletarPorId(id);
    }
}
