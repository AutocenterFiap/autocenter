package br.com.autocenterfiap.ordemservico.application.port;

import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;

import java.util.Collection;
import java.util.Optional;

public interface OSItemServicoRepositoryPort {

    PageResult<OSItemServico> findAllFinalizados(PaginationRequest pagination);

    PageResult<OSItemServico> findByOrdemServicoId(Long ordermServicoId, PaginationRequest pagination);

    PageResult<OSItemServico> findAllByIdIn(Collection<Long> ids, PaginationRequest pagination);

    Optional<OSItemServico> findByServicoIdAndOrdemServicoId(Long servicoId, Long ordermServicoId);

    boolean existsByServicoId(Long servicoId);

    OSItemServico save(OSItemServico item);

    void deleteById(Long id);

}
