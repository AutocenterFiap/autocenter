package br.com.autocenterfiap.ordemservico.application.usecase.OSItemProdutoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemProduto.OSItemProdutoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OSItemProdutoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemProduto;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.produto.domain.entity.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarTodosPorOrdemServicoUseCase (Produto) - Testes Unitários")
class ListarTodosPorOrdemServicoUseCaseTest {

    @Mock
    private OSItemProdutoRepositoryPort itemProdutoRepositoryPort;

    @InjectMocks
    private ListarTodosPorOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve listar os itens de produto da ordem de serviço mapeados para output")
    void deveListarItensDaOrdemServico() {
        PaginationRequest pagination = new PaginationRequest(0, 10);
        OrdemServico ordemServico = OrdemServico.builder().id(1L).build();
        Produto produto = Produto.builder()
                .id(30L).codigo("P-30").nome("Filtro")
                .precoUnitario(BigDecimal.valueOf(50)).build();
        OSItemProduto item = OSItemProduto.builder()
                .id(500L).ordemServico(ordemServico).produto(produto)
                .quantidade(2).precoUnitarioNoMomento(BigDecimal.valueOf(50)).build();
        PageResult<OSItemProduto> page = new PageResult<>(List.of(item), 0, 10, 1, 1);

        when(itemProdutoRepositoryPort.findByOrdemServicoId(eq(1L), eq(pagination))).thenReturn(page);

        PageResult<OSItemProdutoOutput> resultado = useCase.executar(1L, pagination);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(500L, resultado.getContent().get(0).id());
        assertEquals(30L, resultado.getContent().get(0).produtoId());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando a ordem de serviço não tiver produtos")
    void deveRetornarPaginaVazia() {
        PaginationRequest pagination = new PaginationRequest(0, 10);
        PageResult<OSItemProduto> page = new PageResult<>(Collections.emptyList(), 0, 10, 0, 0);

        when(itemProdutoRepositoryPort.findByOrdemServicoId(eq(1L), eq(pagination))).thenReturn(page);

        PageResult<OSItemProdutoOutput> resultado = useCase.executar(1L, pagination);

        assertTrue(resultado.getContent().isEmpty());
    }
}
