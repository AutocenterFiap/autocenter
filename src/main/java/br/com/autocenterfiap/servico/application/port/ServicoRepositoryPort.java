package br.com.autocenterfiap.servico.application.port;

import br.com.autocenterfiap.servico.application.dto.PageResult;
import br.com.autocenterfiap.servico.application.dto.PaginationRequest;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;

import java.util.Optional;

public interface ServicoRepositoryPort {
    Optional<Servico> buscarPorId(Long id);
    PageResult<Servico> listarTodos(PaginationRequest pagination);
    PageResult<Servico> listarPorStatus(StatusServico status, PaginationRequest pagination);
    Servico salvar(Servico servico);
    boolean existeOrdemServicoAssociada(Long id);
    void deletarPorId(Long id);
}
