package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.dto.ServicoOutput;
import br.com.autocenterfiap.servico.application.mapper.ServicoApplicationMapper;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;

public class BuscarServicoPorIdUseCase {

    private final ServicoRepositoryPort repositoryPort;

    public BuscarServicoPorIdUseCase(ServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public ServicoOutput executar(Long id) {
        Servico servico = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException("Servico não encontrado de ID " + id));
        return ServicoApplicationMapper.toOutput(servico);
    }
}
