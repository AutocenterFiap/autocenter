package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.exception.OrcamentoNaoEncontradoException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarOrcamentoPorIdUseCase - Testes Unitários")
class BuscarOrcamentoPorIdUseCaseTest {

    @Mock
    private OrcamentoRepositoryPort orcamentoRepositoryPort;

    @InjectMocks
    private BuscarOrcamentoPorIdUseCase buscarOrcamentoPorIdUseCase;

    @Test
    @DisplayName("Deve retornar OrcamentoOutput quando ID existir")
    void deveRetornarOrcamentoOutputQuandoIdExistir() {
        Orcamento orcamento = Orcamento.builder()
                .id(1L)
                .ordemServicoId(10L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        when(orcamentoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(orcamento));

        OrcamentoOutput output = buscarOrcamentoPorIdUseCase.executar(1L);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals(10L, output.getOrdemServicoId());
        assertEquals(StatusOrcamento.AGUARDANDO_APROVACAO, output.getStatusOrcamento());
    }

    @Test
    @DisplayName("Deve lançar OrcamentoNaoEncontradoException quando ID não existir")
    void deveLancarOrcamentoNaoEncontradoExceptionQuandoIdNaoExistir() {
        when(orcamentoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class,
                () -> buscarOrcamentoPorIdUseCase.executar(99L));
    }
}
