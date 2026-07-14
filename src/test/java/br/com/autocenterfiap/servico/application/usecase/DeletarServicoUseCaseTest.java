package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import br.com.autocenterfiap.servico.domain.exception.ServicoEmUsoException;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;
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
@DisplayName("DeletarServicoUseCase - Testes Unitários")
class DeletarServicoUseCaseTest {

    @Mock
    private ServicoRepositoryPort repositoryPort;

    @InjectMocks
    private DeletarServicoUseCase deletarServicoUseCase;

    @Test
    @DisplayName("Deve deletar serviço com sucesso quando ID existir e não estiver em uso")
    void deveDeletarServicoComSucesso() {
        Servico servico = Servico.builder()
                .id(1L)
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100.00))
                .build();

        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(servico));
        when(repositoryPort.existeOrdemServicoAssociada(1L)).thenReturn(false);

        assertDoesNotThrow(() -> deletarServicoUseCase.executar(1L));

        verify(repositoryPort, times(1)).buscarPorId(1L);
        verify(repositoryPort, times(1)).existeOrdemServicoAssociada(1L);
        verify(repositoryPort, times(1)).deletarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar ServicoNaoEncontradoException ao deletar serviço inexistente")
    void deveLancarServicoNaoEncontradoExceptionAoDeletarInexistente() {
        when(repositoryPort.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoEncontradoException.class, () -> deletarServicoUseCase.executar(999L));

        verify(repositoryPort, times(1)).buscarPorId(999L);
        verify(repositoryPort, never()).existeOrdemServicoAssociada(anyLong());
        verify(repositoryPort, never()).deletarPorId(anyLong());
    }

    @Test
    @DisplayName("Deve lançar ServicoEmUsoException ao deletar serviço associado a ordem de serviço")
    void deveLancarServicoEmUsoExceptionAoDeletarServicoEmUso() {
        Servico servico = Servico.builder()
                .id(1L)
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100.00))
                .build();

        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(servico));
        when(repositoryPort.existeOrdemServicoAssociada(1L)).thenReturn(true);

        assertThrows(ServicoEmUsoException.class, () -> deletarServicoUseCase.executar(1L));

        verify(repositoryPort, times(1)).buscarPorId(1L);
        verify(repositoryPort, times(1)).existeOrdemServicoAssociada(1L);
        verify(repositoryPort, never()).deletarPorId(anyLong());
    }
}
