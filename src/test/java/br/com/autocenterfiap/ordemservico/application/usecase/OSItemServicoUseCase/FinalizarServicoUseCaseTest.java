package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OSItemServicoNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FinalizarServicoUseCase - Testes Unitários")
class FinalizarServicoUseCaseTest {

    @Mock
    private OSItemServicoRepositoryPort itemServicoRepositoryPort;

    @InjectMocks
    private FinalizarServicoUseCase useCase;

    private OSItemServico itemComStatus(StatusItemServico status) {
        return OSItemServico.builder()
                .id(700L)
                .servico(Servico.builder().id(50L).descricao("Alinhamento").build())
                .statusServico(status)
                .dataHoraInicio(LocalDateTime.now().minusMinutes(30))
                .build();
    }

    @Test
    @DisplayName("Deve finalizar o serviço em execução e registrar a data de fim")
    void deveFinalizarServicoEmExecucao() {
        OSItemServico item = itemComStatus(StatusItemServico.EXECUTANDO);
        when(itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(50L, 1L)).thenReturn(Optional.of(item));
        when(itemServicoRepositoryPort.save(any(OSItemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OSItemServicoOutput output = useCase.executar(50L, 1L);

        assertEquals(StatusItemServico.FINALIZADO, output.statusItemServico());
        assertNotNull(output.dataHoraFim());
    }

    @Test
    @DisplayName("Deve lançar StatusOSInvalidoException quando o serviço não estiver em execução")
    void deveLancarExcecaoQuandoStatusInvalido() {
        OSItemServico item = itemComStatus(StatusItemServico.AGUARDANDO_INICIO);
        when(itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(50L, 1L)).thenReturn(Optional.of(item));

        assertThrows(StatusOSInvalidoException.class, () -> useCase.executar(50L, 1L));
        verify(itemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar OSItemServicoNaoEncontradoException quando o item não existir")
    void deveLancarExcecaoQuandoItemNaoEncontrado() {
        when(itemServicoRepositoryPort.findByServicoIdAndOrdemServicoId(50L, 1L)).thenReturn(Optional.empty());

        assertThrows(OSItemServicoNaoEncontradoException.class, () -> useCase.executar(50L, 1L));
        verify(itemServicoRepositoryPort, never()).save(any());
    }
}
