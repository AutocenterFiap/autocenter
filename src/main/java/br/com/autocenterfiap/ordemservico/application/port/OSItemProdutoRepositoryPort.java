package br.com.autocenterfiap.ordemservico.application.port;

import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;

import java.math.BigDecimal;
import java.util.Optional;

public interface OSItemProdutoRepositoryPort {

    PageResult<OSItemProduto> findByOrdemServicoId(Long ordemServicoId, PaginationRequest pagination);

    Optional<OSItemProduto> findByOrdemServicoIdAndProdutoId(Long ordemServicoId, Long produtoId);

    BigDecimal calcularTotalProdutosPorOS(Long ordemServicoId);

    OSItemProduto save(OSItemProduto item);
}
