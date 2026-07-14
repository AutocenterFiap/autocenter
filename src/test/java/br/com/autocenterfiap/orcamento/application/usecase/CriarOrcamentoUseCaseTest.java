package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.CriarOrcamentoInput;
import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarOrcamentoUseCase - Testes Unitários")
class CriarOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepositoryPort orcamentoRepositoryPort;

    @InjectMocks
    private CriarOrcamentoUseCase criarOrcamentoUseCase;

    @Test
    @DisplayName("Deve criar orçamento com sucesso com input válido")
    void deveCriarOrcamentoComSucesso() {
        CriarOrcamentoInput input = CriarOrcamentoInput.builder()
                .ordemServicoId(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        Orcamento orcamentoSalvo = Orcamento.builder()
                .id(1L)
                .ordemServicoId(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        when(orcamentoRepositoryPort.salvar(any(Orcamento.class))).thenReturn(orcamentoSalvo);

        OrcamentoOutput output = criarOrcamentoUseCase.executar(input);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals(BigDecimal.valueOf(500), output.getValorTotal());
        assertEquals(StatusOrcamento.AGUARDANDO_APROVACAO, output.getStatusOrcamento());
        verify(orcamentoRepositoryPort, times(1)).salvar(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando ordemServicoId for nulo")
    void deveLancarExcecaoQuandoOrdemServicoIdNulo() {
        CriarOrcamentoInput input = CriarOrcamentoInput.builder()
                .ordemServicoId(null)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        assertThrows(IllegalArgumentException.class, () -> criarOrcamentoUseCase.executar(input));
        verify(orcamentoRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando valorTotal for zero")
    void deveLancarExcecaoQuandoValorTotalInvalido() {
        CriarOrcamentoInput input = CriarOrcamentoInput.builder()
                .ordemServicoId(1L)
                .valorTotal(BigDecimal.ZERO)
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        assertThrows(IllegalArgumentException.class, () -> criarOrcamentoUseCase.executar(input));
        verify(orcamentoRepositoryPort, never()).salvar(any());
    }
}
