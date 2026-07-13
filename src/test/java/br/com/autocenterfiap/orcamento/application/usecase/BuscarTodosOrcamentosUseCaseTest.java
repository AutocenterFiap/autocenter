package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.dto.PageResult;
import br.com.autocenterfiap.orcamento.application.dto.PaginationRequest;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarTodosOrcamentosUseCase - Testes Unitários")
class BuscarTodosOrcamentosUseCaseTest {

    @Mock
    private OrcamentoRepositoryPort orcamentoRepositoryPort;

    @InjectMocks
    private BuscarTodosOrcamentosUseCase buscarTodosOrcamentosUseCase;

    @Test
    @DisplayName("Deve retornar PageResult com orçamentos")
    void deveRetornarPageResultComOrcamentos() {
        PaginationRequest pagination = new PaginationRequest(0, 10);

        Orcamento orcamento = Orcamento.builder()
                .id(1L)
                .ordemServicoId(10L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        PageResult<Orcamento> pageDomain = new PageResult<>(List.of(orcamento), 0, 10, 1L, 1L);

        when(orcamentoRepositoryPort.buscarPorStatusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO, pagination))
                .thenReturn(pageDomain);

        PageResult<OrcamentoOutput> result = buscarTodosOrcamentosUseCase.executar(
                StatusOrcamento.AGUARDANDO_APROVACAO, pagination);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(StatusOrcamento.AGUARDANDO_APROVACAO, result.getContent().get(0).getStatusOrcamento());
    }

    @Test
    @DisplayName("Deve retornar PageResult vazio quando não houver orçamentos")
    void deveRetornarPageResultVazioQuandoNaoHouverOrcamentos() {
        PaginationRequest pagination = new PaginationRequest(0, 10);

        PageResult<Orcamento> pageVazia = new PageResult<>(List.of(), 0, 10, 0L, 0L);

        when(orcamentoRepositoryPort.buscarPorStatusOrcamento(StatusOrcamento.APROVADO, pagination))
                .thenReturn(pageVazia);

        PageResult<OrcamentoOutput> result = buscarTodosOrcamentosUseCase.executar(
                StatusOrcamento.APROVADO, pagination);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
}
