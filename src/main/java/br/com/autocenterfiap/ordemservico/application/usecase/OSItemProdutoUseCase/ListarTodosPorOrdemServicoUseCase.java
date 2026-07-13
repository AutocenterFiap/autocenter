package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.mapper.OSItemProdutoApplicationMapper;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class ListarTodosPorOrdemServicoUseCase {

    private final OSItemProdutoRepositoryPort itemProdutoRepositoryPort;

    public ListarTodosPorOrdemServicoUseCase(OSItemProdutoRepositoryPort itemProdutoRepositoryPort) {
        this.itemProdutoRepositoryPort = itemProdutoRepositoryPort;
    }

    @Transactional(readOnly = true)
    public PageResult<OSItemProdutoOutput> executar(Long ordemServicoId, PaginationRequest pagination) {
        return this.itemProdutoRepositoryPort.findByOrdemServicoId(ordemServicoId, pagination)
                .map(OSItemProdutoApplicationMapper::toOutput);
    }
}
