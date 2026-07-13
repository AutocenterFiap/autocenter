package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.adapter.in.dto.OSItemServicoRequestDTO;
import br.com.autocenterfiap.ordemservico.adapter.in.dto.OSItemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.application.exception.OSItemServicoNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSItemInvalidoException;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OSItemServicoJpaRepository;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import br.com.autocenterfiap.servico.domain.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.entity.ServicoJpaEntity;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.repository.ServicoJpaRepository;
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

import br.com.autocenterfiap.ordemservico.adapter.in.dto.MetricaTempoGastoServicoDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OSItemServicoService - Testes Unitários")
class OSItemServicoJpaEntityServiceTest {

    @Mock
    private OSItemServicoJpaRepository osItemServicoJpaRepository;

    @Mock
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    @Mock
    private ServicoJpaRepository servicoJpaRepository;

    @InjectMocks
    private OSItemServicoService osItemServicoService;

    private OrdemServicoJpaEntity ordemServicoJpaEntity;
    private ServicoJpaEntity servico;
    private OSItemServicoJpaEntity osItemServicoJpaEntity;

    @BeforeEach
    void setUp() {
        ordemServicoJpaEntity = new OrdemServicoJpaEntity();
        ordemServicoJpaEntity.setId(1L);

        servico = new ServicoJpaEntity();
        servico.setId(5L);
        servico.setDescricao("Troca de óleo");
        servico.setValor(BigDecimal.valueOf(150.00));
        servico.setStatus(StatusServico.ATIVO);

        osItemServicoJpaEntity = new OSItemServicoJpaEntity();
        osItemServicoJpaEntity.setId(10L);
        osItemServicoJpaEntity.setOrdemServicoJpaEntity(ordemServicoJpaEntity);
        osItemServicoJpaEntity.setServico(servico);
        osItemServicoJpaEntity.setValorItemServico(BigDecimal.valueOf(150.00));
        osItemServicoJpaEntity.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);
        osItemServicoJpaEntity.setDataHoraInicio(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve listar todos os serviços de uma ordem de serviço")
    void deveListarTodosServicos() {
        List<OSItemServicoJpaEntity> itens = List.of(osItemServicoJpaEntity);
        when(osItemServicoJpaRepository.findByOrdemServicoJpaEntityId(1L, )).thenReturn(itens);

        List<OSItemServicoResponseDTO> resultado = osItemServicoService.listarPorOS(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().id()).isEqualTo(10L);
        verify(osItemServicoJpaRepository).findByOrdemServicoJpaEntityId(1L, );
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há serviços na OS")
    void deveRetornarListaVaziaQuandoNaoHaServicos() {
        when(osItemServicoJpaRepository.findByOrdemServicoJpaEntityId(1L, )).thenReturn(List.of());

        List<OSItemServicoResponseDTO> resultado = osItemServicoService.listarPorOS(1L);

        assertThat(resultado).isEmpty();
        verify(osItemServicoJpaRepository).findByOrdemServicoJpaEntityId(1L, );
    }

    @Test
    @DisplayName("Deve adicionar serviço ativo na ordem de serviço")
    void deveAdicionarServicoNaOS() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        OSItemServicoRequestDTO dto = new OSItemServicoRequestDTO(5L);

        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));
        when(servicoJpaRepository.findById(5L)).thenReturn(Optional.of(servico));
        when(osItemServicoJpaRepository.save(any(OSItemServicoJpaEntity.class))).thenReturn(osItemServicoJpaEntity);

        OSItemServicoResponseDTO resultado = osItemServicoService.adicionarServicoNaOS(1L, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.valorItemServico()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        verify(ordemServicoJpaRepository).findById(1L);
        verify(servicoJpaRepository).findById(5L);
        verify(osItemServicoJpaRepository).save(any(OSItemServicoJpaEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar serviço inativo")
    void deveLancarExcecaoAoAdicionarServicoInativo() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        servico.setStatus(StatusServico.INATIVO);
        OSItemServicoRequestDTO dto = new OSItemServicoRequestDTO(5L);

        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));
        when(servicoJpaRepository.findById(5L)).thenReturn(Optional.of(servico));

        assertThatThrownBy(() -> osItemServicoService.adicionarServicoNaOS(1L, dto))
                .isInstanceOf(ServicoInativoException.class)
                .hasMessageContaining("Troca de óleo");

        verify(ordemServicoJpaRepository).findById(1L);
        verify(servicoJpaRepository).findById(5L);
        verify(osItemServicoJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar serviço em OS com status inválido")
    void deveLancarExcecaoAoAdicionarServicoEmOSComStatusInvalido() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.ABERTA);
        OSItemServicoRequestDTO dto = new OSItemServicoRequestDTO(5L);

        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));

        assertThatThrownBy(() -> osItemServicoService.adicionarServicoNaOS(1L, dto))
                .isInstanceOf(StatusOSInvalidoException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");

        verify(ordemServicoJpaRepository).findById(1L);
        verify(servicoJpaRepository, never()).findById(any());
        verify(osItemServicoJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve iniciar serviço com sucesso")
    void deveIniciarServico() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_EXECUCAO);
        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.of(osItemServicoJpaEntity));
        when(osItemServicoJpaRepository.save(any(OSItemServicoJpaEntity.class))).thenReturn(osItemServicoJpaEntity);

        OSItemServicoResponseDTO resultado = osItemServicoService.iniciarServico(1L, 5L);

        assertThat(resultado).isNotNull();
        verify(ordemServicoJpaRepository).findById(1L);
        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        verify(osItemServicoJpaRepository).save(any(OSItemServicoJpaEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao iniciar serviço não encontrado")
    void deveLancarExcecaoAoIniciarServicoNaoEncontrado() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_EXECUCAO);
        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> osItemServicoService.iniciarServico(1L, 5L))
                .isInstanceOf(OSItemServicoNaoEncontradoException.class);

        verify(ordemServicoJpaRepository).findById(1L);
        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        verify(osItemServicoJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao iniciar serviço em OS com status inválido")
    void deveLancarExcecaoAoIniciarServicoEmOSComStatusInvalido() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));

        assertThatThrownBy(() -> osItemServicoService.iniciarServico(1L, 5L))
                .isInstanceOf(StatusOSInvalidoException.class)
                .hasMessageContaining("EM_EXECUCAO");

        verify(ordemServicoJpaRepository).findById(1L);
        verify(osItemServicoJpaRepository, never()).findById(any());
        verify(osItemServicoJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao iniciar serviço que não está em AGUARDANDO_INICIO")
    void deveLancarExcecaoAoIniciarServicoComStatusInvalido() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_EXECUCAO);
        osItemServicoJpaEntity.setStatusServico(StatusItemServico.EXECUTANDO);

        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.of(osItemServicoJpaEntity));

        assertThatThrownBy(() -> osItemServicoService.iniciarServico(1L, 5L))
                .isInstanceOf(StatusOSItemInvalidoException.class)
                .hasMessageContaining("AGUARDANDO_INICIO");

        verify(ordemServicoJpaRepository).findById(1L);
        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        verify(osItemServicoJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve finalizar serviço com sucesso")
    void deveFinalizarServico() {
        osItemServicoJpaEntity.setStatusServico(StatusItemServico.EXECUTANDO);
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.of(osItemServicoJpaEntity));
        when(osItemServicoJpaRepository.save(any(OSItemServicoJpaEntity.class))).thenReturn(osItemServicoJpaEntity);

        OSItemServicoResponseDTO resultado = osItemServicoService.finalizarServico(1L, 5L);

        assertThat(resultado).isNotNull();
        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        verify(osItemServicoJpaRepository).save(any(OSItemServicoJpaEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar serviço não encontrado")
    void deveLancarExcecaoAoFinalizarServicoNaoEncontrado() {
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> osItemServicoService.finalizarServico(1L, 5L))
                .isInstanceOf(OSItemServicoNaoEncontradoException.class);

        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        verify(osItemServicoJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar serviço que não está em EXECUTANDO")
    void deveLancarExcecaoAoFinalizarServicoComStatusInvalido() {
        osItemServicoJpaEntity.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.of(osItemServicoJpaEntity));
        assertThatThrownBy(() -> osItemServicoService.finalizarServico(1L, 5L))
                .isInstanceOf(StatusOSInvalidoException.class)
                .hasMessageContaining("EXECUTANDO");

        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        verify(osItemServicoJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover serviço da OS com sucesso")
    void deveRemoverServicoDaOS() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        ordemServicoJpaEntity.getOsItensServicos().add(osItemServicoJpaEntity);

        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.of(osItemServicoJpaEntity));

        osItemServicoService.removerServicoDaOS(1L, 5L);

        verify(ordemServicoJpaRepository).findById(1L);
        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        assertEquals(0, ordemServicoJpaEntity.getOsItensServicos().size(), "O item deveria ter sido removido da lista");
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover serviço não encontrado")
    void deveLancarExcecaoAoRemoverServicoNaoEncontrado() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> osItemServicoService.removerServicoDaOS(1L, 5L))
                .isInstanceOf(OSItemServicoNaoEncontradoException.class);

        verify(ordemServicoJpaRepository).findById(1L);
        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        verify(osItemServicoJpaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover serviço de OS com status inválido")
    void deveLancarExcecaoAoRemoverServicoDeOSComStatusInvalido() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.FINALIZADA);
        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));

        assertThatThrownBy(() -> osItemServicoService.removerServicoDaOS(1L, 5L))
                .isInstanceOf(StatusOSInvalidoException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");

        verify(ordemServicoJpaRepository).findById(1L);
        verify(osItemServicoJpaRepository, never()).findById(any());
        verify(osItemServicoJpaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover serviço que não está em AGUARDANDO_INICIO")
    void deveLancarExcecaoAoRemoverServicoComStatusInvalido() {
        ordemServicoJpaEntity.setStatusOS(StatusOS.EM_DIAGNOSTICO);
        osItemServicoJpaEntity.setStatusServico(StatusItemServico.EXECUTANDO);

        when(ordemServicoJpaRepository.findById(1L)).thenReturn(Optional.of(ordemServicoJpaEntity));
        when(osItemServicoJpaRepository.findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L)).thenReturn(Optional.of(osItemServicoJpaEntity));

        assertThatThrownBy(() -> osItemServicoService.removerServicoDaOS(1L, 5L))
                .isInstanceOf(StatusOSItemInvalidoException.class)
                .hasMessageContaining("AGUARDANDO_INICIO");

        verify(ordemServicoJpaRepository).findById(1L);
        verify(osItemServicoJpaRepository).findByServicoIdAndOrdemServicoJpaEntityId(5L, 1L);
        verify(osItemServicoJpaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há serviços finalizados")
    void getMetricaTempoGastoServico_deveRetornarListaVaziaQuandoSemServicosFinalizados() {
        osItemServicoJpaEntity.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);
        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(osItemServicoJpaEntity));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).isEmpty();
        verify(osItemServicoJpaRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há itens no repositório")
    void getMetricaTempoGastoServico_deveRetornarListaVaziaQuandoRepositorioVazio() {
        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of());

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).isEmpty();
        verify(osItemServicoJpaRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar métricas de tempo quando há serviços finalizados")
    void getMetricaTempoGastoServico_deveRetornarMetricasQuandoHaServicosFinalizados() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 8, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 1, 1, 9, 0, 0);

        osItemServicoJpaEntity.setStatusServico(StatusItemServico.FINALIZADO);
        osItemServicoJpaEntity.setDataHoraInicio(inicio);
        osItemServicoJpaEntity.setDataHoraFim(fim);

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(osItemServicoJpaEntity));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nomeServico()).isEqualTo("Troca de óleo");
        assertThat(resultado.getFirst().tempoGastoMinutos()).isEqualTo("01:00:00");
        verify(osItemServicoJpaRepository).findAll();
    }

    @Test
    @DisplayName("Deve agrupar e calcular média de tempo para o mesmo serviço com múltiplas execuções")
    void getMetricaTempoGastoServico_deveCalcularMediaParaMesmoServico() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        OSItemServicoJpaEntity item1 = new OSItemServicoJpaEntity();
        item1.setServico(servico);
        item1.setStatusServico(StatusItemServico.FINALIZADO);
        item1.setDataHoraInicio(base);
        item1.setDataHoraFim(base.plusHours(1));

        OSItemServicoJpaEntity item2 = new OSItemServicoJpaEntity();
        item2.setServico(servico);
        item2.setStatusServico(StatusItemServico.FINALIZADO);
        item2.setDataHoraInicio(base);
        item2.setDataHoraFim(base.plusHours(3));

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(item1, item2));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nomeServico()).isEqualTo("Troca de óleo");
        assertThat(resultado.getFirst().tempoGastoMinutos()).isEqualTo("02:00:00");
        verify(osItemServicoJpaRepository).findAll();
    }

    @Test
    @DisplayName("Deve agrupar métricas por serviços diferentes")
    void getMetricaTempoGastoServico_deveAgruparPorServicoDiferente() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        ServicoJpaEntity servico2 = new ServicoJpaEntity();
        servico2.setId(6L);
        servico2.setDescricao("Alinhamento");
        servico2.setValor(BigDecimal.valueOf(80.00));
        servico2.setStatus(br.com.autocenterfiap.servico.domain.enums.StatusServico.ATIVO);

        OSItemServicoJpaEntity itemTrocaOleo = new OSItemServicoJpaEntity();
        itemTrocaOleo.setServico(servico);
        itemTrocaOleo.setStatusServico(StatusItemServico.FINALIZADO);
        itemTrocaOleo.setDataHoraInicio(base);
        itemTrocaOleo.setDataHoraFim(base.plusHours(1));

        OSItemServicoJpaEntity itemAlinhamento = new OSItemServicoJpaEntity();
        itemAlinhamento.setServico(servico2);
        itemAlinhamento.setStatusServico(StatusItemServico.FINALIZADO);
        itemAlinhamento.setDataHoraInicio(base);
        itemAlinhamento.setDataHoraFim(base.plusMinutes(30));

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(itemTrocaOleo, itemAlinhamento));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(2);

        MetricaTempoGastoServicoDTO metricaTrocaOleo = resultado.stream()
                .filter(m -> m.nomeServico().equals("Troca de óleo"))
                .findFirst()
                .orElseThrow();

        MetricaTempoGastoServicoDTO metricaAlinhamento = resultado.stream()
                .filter(m -> m.nomeServico().equals("Alinhamento"))
                .findFirst()
                .orElseThrow();

        assertThat(metricaTrocaOleo.tempoGastoMinutos()).isEqualTo("01:00:00");
        assertThat(metricaAlinhamento.tempoGastoMinutos()).isEqualTo("00:30:00");
        verify(osItemServicoJpaRepository).findAll();
    }

    @Test
    @DisplayName("Deve ignorar serviços que não estão com status FINALIZADO no cálculo de métricas")
    void getMetricaTempoGastoServico_deveIgnorarServicosNaoFinalizados() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        OSItemServicoJpaEntity itemFinalizado = new OSItemServicoJpaEntity();
        itemFinalizado.setServico(servico);
        itemFinalizado.setStatusServico(StatusItemServico.FINALIZADO);
        itemFinalizado.setDataHoraInicio(base);
        itemFinalizado.setDataHoraFim(base.plusHours(2));

        OSItemServicoJpaEntity itemExecutando = new OSItemServicoJpaEntity();
        itemExecutando.setServico(servico);
        itemExecutando.setStatusServico(StatusItemServico.EXECUTANDO);
        itemExecutando.setDataHoraInicio(base);

        OSItemServicoJpaEntity itemAguardando = new OSItemServicoJpaEntity();
        itemAguardando.setServico(servico);
        itemAguardando.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);
        itemAguardando.setDataHoraInicio(base);

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(itemFinalizado, itemExecutando, itemAguardando));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nomeServico()).isEqualTo("Troca de óleo");
        assertThat(resultado.getFirst().tempoGastoMinutos()).isEqualTo("02:00:00");
        verify(osItemServicoJpaRepository).findAll();
    }

    @Test
    @DisplayName("Deve formatar tempo com 0 segundos corretamente")
    void getMetricaTempoGastoServico_deveFormatarTempoZeroCorretamente() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        osItemServicoJpaEntity.setStatusServico(StatusItemServico.FINALIZADO);
        osItemServicoJpaEntity.setDataHoraInicio(base);
        osItemServicoJpaEntity.setDataHoraFim(base);

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(osItemServicoJpaEntity));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().tempoGastoMinutos()).isEqualTo("00:00:00");
    }

    @Test
    @DisplayName("Deve formatar tempo com apenas segundos corretamente")
    void getMetricaTempoGastoServico_deveFormatarApenasSegundosCorretamente() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        osItemServicoJpaEntity.setStatusServico(StatusItemServico.FINALIZADO);
        osItemServicoJpaEntity.setDataHoraInicio(base);
        osItemServicoJpaEntity.setDataHoraFim(base.plusSeconds(45));

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(osItemServicoJpaEntity));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().tempoGastoMinutos()).isEqualTo("00:00:45");
    }

    @Test
    @DisplayName("Deve formatar tempo com horas, minutos e segundos corretamente")
    void getMetricaTempoGastoServico_deveFormatarTempoCompletoCorretamente() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        osItemServicoJpaEntity.setStatusServico(StatusItemServico.FINALIZADO);
        osItemServicoJpaEntity.setDataHoraInicio(base);
        osItemServicoJpaEntity.setDataHoraFim(base.plusHours(2).plusMinutes(30).plusSeconds(15)); // 2h30m15s

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(osItemServicoJpaEntity));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().tempoGastoMinutos()).isEqualTo("02:30:15");
    }

    @Test
    @DisplayName("Deve calcular média correta para múltiplas execuções do mesmo serviço")
    void calculaMediaServicos_deveCalcularMediaCorretaParaMultiplasExecucoes() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        OSItemServicoJpaEntity item1 = new OSItemServicoJpaEntity();
        item1.setServico(servico);
        item1.setStatusServico(StatusItemServico.FINALIZADO);
        item1.setDataHoraInicio(base);
        item1.setDataHoraFim(base.plusMinutes(30));

        OSItemServicoJpaEntity item2 = new OSItemServicoJpaEntity();
        item2.setServico(servico);
        item2.setStatusServico(StatusItemServico.FINALIZADO);
        item2.setDataHoraInicio(base);
        item2.setDataHoraFim(base.plusMinutes(60));

        OSItemServicoJpaEntity item3 = new OSItemServicoJpaEntity();
        item3.setServico(servico);
        item3.setStatusServico(StatusItemServico.FINALIZADO);
        item3.setDataHoraInicio(base);
        item3.setDataHoraFim(base.plusMinutes(90));

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(item1, item2, item3));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nomeServico()).isEqualTo("Troca de óleo");
        assertThat(resultado.getFirst().tempoGastoMinutos()).isEqualTo("01:00:00");
    }

    @Test
    @DisplayName("Deve excluir itens com status diferente de FINALIZADO do cálculo da média")
    void calculaMediaServicos_deveExcluirItensNaoFinalizados() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        OSItemServicoJpaEntity itemFinalizado = new OSItemServicoJpaEntity();
        itemFinalizado.setServico(servico);
        itemFinalizado.setStatusServico(StatusItemServico.FINALIZADO);
        itemFinalizado.setDataHoraInicio(base);
        itemFinalizado.setDataHoraFim(base.plusHours(2));

        OSItemServicoJpaEntity itemExecutando = new OSItemServicoJpaEntity();
        itemExecutando.setServico(servico);
        itemExecutando.setStatusServico(StatusItemServico.EXECUTANDO);
        itemExecutando.setDataHoraInicio(base);

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(itemFinalizado, itemExecutando));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().tempoGastoMinutos()).isEqualTo("02:00:00");
    }

    @Test
    @DisplayName("Deve retornar nomeServico correto para cada serviço na métrica")
    void getMetricaTempoGastoServico_deveRetornarNomeServicoCorreto() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 8, 0, 0);

        osItemServicoJpaEntity.setStatusServico(StatusItemServico.FINALIZADO);
        osItemServicoJpaEntity.setDataHoraInicio(base);
        osItemServicoJpaEntity.setDataHoraFim(base.plusHours(1));

        when(osItemServicoJpaRepository.findAll()).thenReturn(List.of(osItemServicoJpaEntity));

        List<MetricaTempoGastoServicoDTO> resultado = osItemServicoService.getMetricaTempoGastoServico();

        assertThat(resultado.getFirst().nomeServico())
                .isEqualTo(servico.getDescricao())
                .isEqualTo("Troca de óleo");
    }
}
