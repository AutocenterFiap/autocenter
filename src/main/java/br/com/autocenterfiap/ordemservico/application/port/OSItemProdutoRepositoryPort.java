package br.com.autocenterfiap.ordemservico.application.port;

import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

public interface OSItemProdutoRepositoryPort {

    PageResult<OSItemProduto> findByOrdemServicoId(Long ordemServicoId, PaginationRequest pagination);

    Optional<OSItemProduto> findByOrdemServicoIdAndProdutoId(Long ordemServicoId, Long produtoId);

    PageResult<OSItemProduto> findAllByIdIn(Collection<Long> ids, PaginationRequest pagination);

    BigDecimal calcularTotalProdutosPorOS(Long ordemServicoId);

    OSItemProduto save(OSItemProduto item);

    void deleteById(Long id);
}
