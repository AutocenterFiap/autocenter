package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.dto.AtualizarServicoInput;
import br.com.autocenterfiap.servico.application.dto.ServicoOutput;
import br.com.autocenterfiap.servico.application.mapper.ServicoApplicationMapper;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;

public class AtualizarServicoUseCase {

    private final ServicoRepositoryPort repositoryPort;

    public AtualizarServicoUseCase(ServicoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public ServicoOutput executar(Long id, AtualizarServicoInput input) {
        Servico servico = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException("Servico não encontrado de ID " + id));

        servico.atualizar(input.getDescricao(), input.getStatus(), input.getValor());
        Servico servicoSalvo = repositoryPort.salvar(servico);
        return ServicoApplicationMapper.toOutput(servicoSalvo);
    }
}
