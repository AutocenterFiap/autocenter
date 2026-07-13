package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.exception.OrcamentoNaoEncontradoException;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReprovarOrcamentoUseCase - Testes Unitários")
class ReprovarOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepositoryPort orcamentoRepositoryPort;

    @Mock
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    @InjectMocks
    private ReprovarOrcamentoUseCase reprovarOrcamentoUseCase;

    private Orcamento orcamento;
    private OrdemServicoJpaEntity ordemServicoJpaEntity;

    @BeforeEach
    void setUp() {
        ordemServicoJpaEntity = OrdemServicoJpaEntity.builder()
                .id(10L)
                .build();

        orcamento = Orcamento.builder()
                .id(1L)
                .ordemServicoId(10L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();
    }

    @Test
    @DisplayName("Deve reprovar orçamento e atualizar status para REPROVADO")
    void deveReprovarOrcamentoEAtualizarStatusParaReprovado() {
        when(orcamentoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepositoryPort.salvar(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordemServicoJpaRepository.findById(10L)).thenReturn(Optional.of(ordemServicoJpaEntity));

        OrcamentoOutput output = reprovarOrcamentoUseCase.executar(1L);

        assertEquals(StatusOrcamento.REPROVADO, output.getStatusOrcamento());
        verify(orcamentoRepositoryPort).salvar(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve cancelar ordem de serviço vinculada ao orçamento")
    void deveCancelarOrdemServicoVinculada() {
        when(orcamentoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepositoryPort.salvar(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordemServicoJpaRepository.findById(10L)).thenReturn(Optional.of(ordemServicoJpaEntity));

        reprovarOrcamentoUseCase.executar(1L);

        verify(ordemServicoJpaRepository).findById(10L);
    }

    @Test
    @DisplayName("Deve lançar OrcamentoNaoEncontradoException quando orçamento não existir")
    void deveLancarExcecaoQuandoOrcamentoNaoEncontrado() {
        when(orcamentoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(OrcamentoNaoEncontradoException.class,
                () -> reprovarOrcamentoUseCase.executar(99L));

        verify(orcamentoRepositoryPort, never()).salvar(any());
    }
}
