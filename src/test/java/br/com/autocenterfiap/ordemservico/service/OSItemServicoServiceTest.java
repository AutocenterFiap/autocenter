package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.dto.OSItemServicoRequestDTO;
import br.com.autocenterfiap.ordemservico.dto.OSItemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.exception.OSItemServicoNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.exception.StatusOSItemInvalidoException;
import br.com.autocenterfiap.ordemservico.model.OSItemServico;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.ordemservico.repository.OSItemServicoRepository;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.servico.enums.StatusServico;
import br.com.autocenterfiap.servico.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.model.Servico;
import br.com.autocenterfiap.servico.service.ServicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OSItemServicoService - Testes Unitários")
class OSItemServicoServiceTest {

    @Mock
    private OSItemServicoRepository osItemServicoRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ServicoService servicoService;

    @InjectMocks
    private OSItemServicoService osItemServicoService;

    private OrdemServico ordemServico;
    private Servico servico;
    private OSItemServico osItemServico;

    @BeforeEach
    void setUp() {
        ordemServico = new OrdemServico();
        ordemServico.setId(1L);

        servico = new Servico();
        servico.setId(5L);
        servico.setDescricao("Troca de óleo");
        servico.setValor(BigDecimal.valueOf(150.00));
        servico.setStatus(StatusServico.ATIVO);

        osItemServico = new OSItemServico();
        osItemServico.setId(10L);
        osItemServico.setOrdemServico(ordemServico);
        osItemServico.setServico(servico);
        osItemServico.setValorItemServico(BigDecimal.valueOf(150.00));
        osItemServico.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);
        osItemServico.setDataHoraInicio(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve listar todos os serviços de uma ordem de serviço")
    void deveListarTodosServicos() {
        List<OSItemServico> itens = List.of(osItemServico);
        when(osItemServicoRepository.findByOrdemServicoId(1L)).thenReturn(itens);

        List<OSItemServicoResponseDTO> resultado = osItemServicoService.listarPorOS(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().id()).isEqualTo(10L);
        verify(osItemServicoRepository).findByOrdemServicoId(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há serviços na OS")
    void deveRetornarListaVaziaQuandoNaoHaServicos() {
        when(osItemServicoRepository.findByOrdemServicoId(1L)).thenReturn(List.of());

        List<OSItemServicoResponseDTO> resultado = osItemServicoService.listarPorOS(1L);

        assertThat(resultado).isEmpty();
        verify(osItemServicoRepository).findByOrdemServicoId(1L);
    }

    @Test
    @DisplayName("Deve adicionar serviço ativo na ordem de serviço")
    void deveAdicionarServicoNaOS() {
        ordemServico.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        OSItemServicoRequestDTO dto = new OSItemServicoRequestDTO(5L);

        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(servicoService.buscarPorId(5L)).thenReturn(servico);
        when(osItemServicoRepository.save(any(OSItemServico.class))).thenReturn(osItemServico);

        OSItemServicoResponseDTO resultado = osItemServicoService.adicionarServicoNaOS(1L, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.valorItemServico()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        verify(ordemServicoRepository).findById(1L);
        verify(servicoService).buscarPorId(5L);
        verify(osItemServicoRepository).save(any(OSItemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar serviço inativo")
    void deveLancarExcecaoAoAdicionarServicoInativo() {
        ordemServico.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        servico.setStatus(StatusServico.INATIVO);
        OSItemServicoRequestDTO dto = new OSItemServicoRequestDTO(5L);

        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(servicoService.buscarPorId(5L)).thenReturn(servico);

        assertThatThrownBy(() -> osItemServicoService.adicionarServicoNaOS(1L, dto))
                .isInstanceOf(ServicoInativoException.class)
                .hasMessageContaining("Troca de óleo");

        verify(ordemServicoRepository).findById(1L);
        verify(servicoService).buscarPorId(5L);
        verify(osItemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar serviço em OS com status inválido")
    void deveLancarExcecaoAoAdicionarServicoEmOSComStatusInvalido() {
        ordemServico.setStatusOS(StatusOS.ABERTA);
        OSItemServicoRequestDTO dto = new OSItemServicoRequestDTO(5L);

        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));

        assertThatThrownBy(() -> osItemServicoService.adicionarServicoNaOS(1L, dto))
                .isInstanceOf(StatusOSInvalidoException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");

        verify(ordemServicoRepository).findById(1L);
        verify(servicoService, never()).buscarPorId(any());
        verify(osItemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve iniciar serviço com sucesso")
    void deveIniciarServico() {
        ordemServico.setStatusOS(StatusOS.EM_EXECUCAO);
        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.of(osItemServico));
        when(osItemServicoRepository.save(any(OSItemServico.class))).thenReturn(osItemServico);

        OSItemServicoResponseDTO resultado = osItemServicoService.iniciarServico(1L, 5L);

        assertThat(resultado).isNotNull();
        verify(ordemServicoRepository).findById(1L);
        verify(osItemServicoRepository).findById(5L);
        verify(osItemServicoRepository).save(any(OSItemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao iniciar serviço não encontrado")
    void deveLancarExcecaoAoIniciarServicoNaoEncontrado() {
        ordemServico.setStatusOS(StatusOS.EM_EXECUCAO);
        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> osItemServicoService.iniciarServico(1L, 5L))
                .isInstanceOf(OSItemServicoNaoEncontradoException.class);

        verify(ordemServicoRepository).findById(1L);
        verify(osItemServicoRepository).findById(5L);
        verify(osItemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao iniciar serviço em OS com status inválido")
    void deveLancarExcecaoAoIniciarServicoEmOSComStatusInvalido() {
        ordemServico.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));

        assertThatThrownBy(() -> osItemServicoService.iniciarServico(1L, 5L))
                .isInstanceOf(StatusOSInvalidoException.class)
                .hasMessageContaining("EM_EXECUCAO");

        verify(ordemServicoRepository).findById(1L);
        verify(osItemServicoRepository, never()).findById(any());
        verify(osItemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao iniciar serviço que não está em AGUARDANDO_INICIO")
    void deveLancarExcecaoAoIniciarServicoComStatusInvalido() {
        ordemServico.setStatusOS(StatusOS.EM_EXECUCAO);
        osItemServico.setStatusServico(StatusItemServico.EXECUTANDO);

        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.of(osItemServico));

        assertThatThrownBy(() -> osItemServicoService.iniciarServico(1L, 5L))
                .isInstanceOf(StatusOSItemInvalidoException.class)
                .hasMessageContaining("AGUARDANDO_INICIO");

        verify(ordemServicoRepository).findById(1L);
        verify(osItemServicoRepository).findById(5L);
        verify(osItemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve finalizar serviço com sucesso")
    void deveFinalizarServico() {
        osItemServico.setStatusServico(StatusItemServico.EXECUTANDO);
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.of(osItemServico));
        when(osItemServicoRepository.save(any(OSItemServico.class))).thenReturn(osItemServico);

        OSItemServicoResponseDTO resultado = osItemServicoService.finalizarServico(1L, 5L);

        assertThat(resultado).isNotNull();
        verify(osItemServicoRepository).findById(5L);
        verify(osItemServicoRepository).save(any(OSItemServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar serviço não encontrado")
    void deveLancarExcecaoAoFinalizarServicoNaoEncontrado() {
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> osItemServicoService.finalizarServico(1L, 5L))
                .isInstanceOf(OSItemServicoNaoEncontradoException.class);

        verify(osItemServicoRepository).findById(5L);
        verify(osItemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar serviço que não está em EXECUTANDO")
    void deveLancarExcecaoAoFinalizarServicoComStatusInvalido() {
        osItemServico.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.of(osItemServico));

        assertThatThrownBy(() -> osItemServicoService.finalizarServico(1L, 5L))
                .isInstanceOf(StatusOSInvalidoException.class)
                .hasMessageContaining("EXECUTANDO");

        verify(osItemServicoRepository).findById(5L);
        verify(osItemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover serviço da OS com sucesso")
    void deveRemoverServicoDaOS() {
        ordemServico.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        ordemServico.getOsItensServicos().add(osItemServico);

        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.of(osItemServico));

        osItemServicoService.removerServicoDaOS(1L, 5L);

        verify(ordemServicoRepository).findById(1L);
        verify(osItemServicoRepository).findById(5L);
        assertEquals(0, ordemServico.getOsItensServicos().size(), "O item deveria ter sido removido da lista");
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover serviço não encontrado")
    void deveLancarExcecaoAoRemoverServicoNaoEncontrado() {
        ordemServico.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> osItemServicoService.removerServicoDaOS(1L, 5L))
                .isInstanceOf(OSItemServicoNaoEncontradoException.class);

        verify(ordemServicoRepository).findById(1L);
        verify(osItemServicoRepository).findById(5L);
        verify(osItemServicoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover serviço de OS com status inválido")
    void deveLancarExcecaoAoRemoverServicoDeOSComStatusInvalido() {
        ordemServico.setStatusOS(StatusOS.FINALIZADA);
        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));

        assertThatThrownBy(() -> osItemServicoService.removerServicoDaOS(1L, 5L))
                .isInstanceOf(StatusOSInvalidoException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");

        verify(ordemServicoRepository).findById(1L);
        verify(osItemServicoRepository, never()).findById(any());
        verify(osItemServicoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover serviço que não está em AGUARDANDO_INICIO")
    void deveLancarExcecaoAoRemoverServicoComStatusInvalido() {
        ordemServico.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        osItemServico.setStatusServico(StatusItemServico.EXECUTANDO);

        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(osItemServicoRepository.findById(5L)).thenReturn(Optional.of(osItemServico));

        assertThatThrownBy(() -> osItemServicoService.removerServicoDaOS(1L, 5L))
                .isInstanceOf(StatusOSItemInvalidoException.class)
                .hasMessageContaining("AGUARDANDO_INICIO");

        verify(ordemServicoRepository).findById(1L);
        verify(osItemServicoRepository).findById(5L);
        verify(osItemServicoRepository, never()).delete(any());
    }
}
