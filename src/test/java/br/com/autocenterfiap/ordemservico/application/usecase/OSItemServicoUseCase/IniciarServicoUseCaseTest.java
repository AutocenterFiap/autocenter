package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OSItemServicoNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSItemInvalidoException;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IniciarServicoUseCase - Testes Unitários")
class IniciarServicoUseCaseTest {

    @Mock
    private OSItemServicoRepositoryPort itemServicoRepositoryPort;
    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private IniciarServicoUseCase useCase;

    private OrdemServico ordemServicoEmExecucao;

    @BeforeEach
    void setUp() {
        ordemServicoEmExecucao = OrdemServico.builder()
                .id(1L).statusOS(StatusOS.EM_EXECUCAO).build();
    }

    private OSItemServico itemComStatus(StatusItemServico status) {
        return OSItemServico.builder()
                .id(700L)
                .servico(Servico.builder().id(50L).descricao("Alinhamento").build())
                .statusServico(status)
                .build();
    }

    @Test
    @DisplayName("Deve iniciar o serviço quando a OS e o item estiverem em estado válido")
    void deveIniciarServicoComSucesso() {
        OSItemServico item = itemComStatus(StatusItemServico.AGUARDANDO_INICIO);
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServicoEmExecucao));
        when(itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(50L, 1L)).thenReturn(Optional.of(item));
        when(itemServicoRepositoryPort.save(any(OSItemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OSItemServicoOutput output = useCase.executar(1L, 50L);

        assertEquals(StatusItemServico.EXECUTANDO, output.statusItemServico());
        assertNotNull(output.dataHoraInicio());
    }

    @Test
    @DisplayName("Deve lançar OrdemServicoNaoEncontradaException quando a OS não existir")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(1L, 50L));
        verify(itemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar StatusOSInvalidoException quando a OS não estiver em execução")
    void deveLancarExcecaoQuandoStatusOSInvalido() {
        OrdemServico osAberta = OrdemServico.builder().id(1L).statusOS(StatusOS.ABERTA).build();
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(osAberta));

        assertThrows(StatusOSInvalidoException.class, () -> useCase.executar(1L, 50L));
        verify(itemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar OSItemServicoNaoEncontradoException quando o item não existir")
    void deveLancarExcecaoQuandoItemNaoEncontrado() {
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServicoEmExecucao));
        when(itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(50L, 1L)).thenReturn(Optional.empty());

        assertThrows(OSItemServicoNaoEncontradoException.class, () -> useCase.executar(1L, 50L));
        verify(itemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar StatusOSItemInvalidoException quando o item não estiver aguardando início")
    void deveLancarExcecaoQuandoStatusItemInvalido() {
        OSItemServico item = itemComStatus(StatusItemServico.EXECUTANDO);
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServicoEmExecucao));
        when(itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(50L, 1L)).thenReturn(Optional.of(item));

        assertThrows(StatusOSItemInvalidoException.class, () -> useCase.executar(1L, 50L));
        verify(itemServicoRepositoryPort, never()).save(any());
    }
}
