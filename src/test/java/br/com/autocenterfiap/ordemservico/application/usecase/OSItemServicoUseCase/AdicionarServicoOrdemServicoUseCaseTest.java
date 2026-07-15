package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import br.com.autocenterfiap.servico.domain.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;
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
@DisplayName("AdicionarServicoOrdemServicoUseCase - Testes Unitários")
class AdicionarServicoOrdemServicoUseCaseTest {

    @Mock
    private OSItemServicoRepositoryPort itemServicoRepositoryPort;
    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;
    @Mock
    private ServicoRepositoryPort servicoRepositoryPort;

    @InjectMocks
    private AdicionarServicoOrdemServicoUseCase useCase;

    private OrdemServico ordemServicoEmDiagnostico;
    private Servico servicoAtivo;

    @BeforeEach
    void setUp() {
        ordemServicoEmDiagnostico = OrdemServico.builder()
                .id(1L).statusOS(StatusOS.EM_DIAGNOSTICO).valorTotal(BigDecimal.ZERO).build();
        servicoAtivo = Servico.builder()
                .id(50L).descricao("Alinhamento").status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(120)).build();
    }

    @Test
    @DisplayName("Deve adicionar serviço na OS e recalcular o valor total")
    void deveAdicionarServicoComSucesso() {
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServicoEmDiagnostico));
        when(servicoRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(servicoAtivo));
        when(itemServicoRepositoryPort.save(any(OSItemServico.class))).thenAnswer(inv -> {
            OSItemServico item = inv.getArgument(0);
            item.setId(700L);
            return item;
        });

        OSItemServicoOutput output = useCase.executar(1L, new OSItemServicoInput(50L));

        assertNotNull(output);
        assertEquals(700L, output.id());
        assertEquals(StatusItemServico.AGUARDANDO_INICIO, output.statusItemServico());
        assertEquals(0, BigDecimal.valueOf(120).compareTo(output.valorItemServico()));
        assertEquals(0, BigDecimal.valueOf(120).compareTo(ordemServicoEmDiagnostico.getValorTotal()));
    }

    @Test
    @DisplayName("Deve lançar OrdemServicoNaoEncontradaException quando a OS não existir")
    void deveLancarExcecaoQuandoOrdemServicoNaoEncontrada() {
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class,
                () -> useCase.executar(1L, new OSItemServicoInput(50L)));
        verify(itemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar StatusOSInvalidoException quando a OS não estiver em diagnóstico")
    void deveLancarExcecaoQuandoStatusOSInvalido() {
        OrdemServico osAberta = OrdemServico.builder()
                .id(1L).statusOS(StatusOS.ABERTA).valorTotal(BigDecimal.ZERO).build();
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(osAberta));

        assertThrows(StatusOSInvalidoException.class,
                () -> useCase.executar(1L, new OSItemServicoInput(50L)));
        verify(itemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ServicoNaoEncontradoException quando o serviço não existir")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServicoEmDiagnostico));
        when(servicoRepositoryPort.buscarPorId(50L)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoEncontradoException.class,
                () -> useCase.executar(1L, new OSItemServicoInput(50L)));
        verify(itemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ServicoInativoException quando o serviço estiver inativo")
    void deveLancarExcecaoQuandoServicoInativo() {
        Servico servicoInativo = Servico.builder()
                .id(50L).descricao("Alinhamento").status(StatusServico.INATIVO)
                .valor(BigDecimal.valueOf(120)).build();
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServicoEmDiagnostico));
        when(servicoRepositoryPort.buscarPorId(50L)).thenReturn(Optional.of(servicoInativo));

        assertThrows(ServicoInativoException.class,
                () -> useCase.executar(1L, new OSItemServicoInput(50L)));
        verify(itemServicoRepositoryPort, never()).save(any());
    }
}
