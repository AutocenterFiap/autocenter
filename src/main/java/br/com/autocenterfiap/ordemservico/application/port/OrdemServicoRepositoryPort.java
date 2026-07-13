package br.com.autocenterfiap.ordemservico.application.port;

import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepositoryPort {

    PageResult<OrdemServico> findAll(PaginationRequest pagination);

    PageResult<OrdemServico> findByStatus(StatusOS status, PaginationRequest pagination);

    Optional<OrdemServico> findById(Long id);

    Optional<OrdemServico> findByNumeroOrdemServico(Long numeroOrdemServico);

    boolean existsByVeiculoId(Long veiculoId);

    boolean existsByClienteId(Long clienteId);

    OrdemServico save(OrdemServico ordemServico);

    void delete(OrdemServico ordemServico);

    boolean existsByVeiculoIdAndStatusOSIn(Long veiculoId, List<StatusOS> statusList);
}
