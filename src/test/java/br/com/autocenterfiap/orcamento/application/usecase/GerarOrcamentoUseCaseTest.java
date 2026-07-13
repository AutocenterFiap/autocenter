package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.CriarOrcamentoInput;
import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GerarOrcamentoUseCase - Testes Unitários")
class GerarOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepositoryPort orcamentoRepositoryPort;

    @Mock
    private CriarOrcamentoUseCase criarOrcamentoUseCase;

    @Mock
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    @InjectMocks
    private GerarOrcamentoUseCase gerarOrcamentoUseCase;

    private OrdemServicoJpaEntity ordemServicoJpaEntity;

    @BeforeEach
    void setUp() {
        ordemServicoJpaEntity = OrdemServicoJpaEntity.builder()
                .id(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOS(StatusOS.AGUARDANDO_APROVACAO)
                .build();
    }

    @Test
    @DisplayName("Deve gerar orçamento para OS aguardando aprovação sem orçamento existente")
    void deveGerarOrcamentoParaOSAguardandoAprovacao() {
        when(ordemServicoJpaRepository.findByStatus(StatusOS.AGUARDANDO_APROVACAO))
                .thenReturn(List.of(ordemServicoJpaEntity));
        when(orcamentoRepositoryPort.buscarOrcamentoAguardandoAprovacaoPorOS(1L))
                .thenReturn(Optional.empty());
        when(criarOrcamentoUseCase.executar(any(CriarOrcamentoInput.class)))
                .thenReturn(OrcamentoOutput.builder()
                        .id(10L)
                        .ordemServicoId(1L)
                        .valorTotal(BigDecimal.valueOf(500))
                        .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                        .build());

        gerarOrcamentoUseCase.executar();

        verify(criarOrcamentoUseCase, times(1)).executar(any(CriarOrcamentoInput.class));
    }

    @Test
    @DisplayName("Não deve gerar orçamento se já existir aguardando aprovação")
    void naoDeveGerarOrcamentoSeJaExistirAguardandoAprovacao() {
        Orcamento orcamentoExistente = Orcamento.builder()
                .id(10L)
                .ordemServicoId(1L)
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();

        when(ordemServicoJpaRepository.findByStatus(StatusOS.AGUARDANDO_APROVACAO))
                .thenReturn(List.of(ordemServicoJpaEntity));
        when(orcamentoRepositoryPort.buscarOrcamentoAguardandoAprovacaoPorOS(1L))
                .thenReturn(Optional.of(orcamentoExistente));

        gerarOrcamentoUseCase.executar();

        verify(criarOrcamentoUseCase, never()).executar(any());
    }

    @Test
    @DisplayName("Deve ignorar quando não houver OS aguardando aprovação")
    void deveIgnorarQuandoNaoHouverOSAguardandoAprovacao() {
        when(ordemServicoJpaRepository.findByStatus(StatusOS.AGUARDANDO_APROVACAO))
                .thenReturn(List.of());

        gerarOrcamentoUseCase.executar();

        verify(criarOrcamentoUseCase, never()).executar(any());
        verify(orcamentoRepositoryPort, never()).buscarOrcamentoAguardandoAprovacaoPorOS(any());
    }
}
