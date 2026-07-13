package br.com.autocenterfiap.orcamento.application.usecase;

import br.com.autocenterfiap.orcamento.application.dto.CriarOrcamentoInput;
import br.com.autocenterfiap.orcamento.application.dto.OrcamentoOutput;
import br.com.autocenterfiap.orcamento.application.port.OrcamentoRepositoryPort;
import br.com.autocenterfiap.orcamento.domain.entity.Orcamento;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GerarOrcamentoUseCase - Testes Unitários")
class GerarOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepositoryPort orcamentoRepositoryPort;

    @Mock
    private CriarOrcamentoUseCase criarOrcamentoUseCase;

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private GerarOrcamentoUseCase gerarOrcamentoUseCase;

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        ordemServico = OrdemServico.builder()
                .id(1L)
                .valorTotal(BigDecimal.valueOf(500))
                .statusOS(StatusOS.AGUARDANDO_APROVACAO)
                .build();
    }

    @Test
    @DisplayName("Deve gerar orçamento para OS aguardando aprovação sem orçamento existente")
    void deveGerarOrcamentoParaOSAguardandoAprovacao() {
        PageResult<OrdemServico> page = new PageResult<>(List.of(ordemServico), 0, 100, 1L, 1L);

        when(ordemServicoRepositoryPort.findByStatus(eq(StatusOS.AGUARDANDO_APROVACAO), any(PaginationRequest.class)))
                .thenReturn(page);
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

        PageResult<OrdemServico> page = new PageResult<>(List.of(ordemServico), 0, 100, 1L, 1L);

        when(ordemServicoRepositoryPort.findByStatus(eq(StatusOS.AGUARDANDO_APROVACAO), any(PaginationRequest.class)))
                .thenReturn(page);
        when(orcamentoRepositoryPort.buscarOrcamentoAguardandoAprovacaoPorOS(1L))
                .thenReturn(Optional.of(orcamentoExistente));

        gerarOrcamentoUseCase.executar();

        verify(criarOrcamentoUseCase, never()).executar(any());
    }

    @Test
    @DisplayName("Deve ignorar quando não houver OS aguardando aprovação")
    void deveIgnorarQuandoNaoHouverOSAguardandoAprovacao() {
        PageResult<OrdemServico> pageVazia = new PageResult<>(List.of(), 0, 100, 0L, 0L);

        when(ordemServicoRepositoryPort.findByStatus(eq(StatusOS.AGUARDANDO_APROVACAO), any(PaginationRequest.class)))
                .thenReturn(pageVazia);

        gerarOrcamentoUseCase.executar();

        verify(criarOrcamentoUseCase, never()).executar(any());
        verify(orcamentoRepositoryPort, never()).buscarOrcamentoAguardandoAprovacaoPorOS(any());
    }
}
