package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.dto.AtualizarServicoInput;
import br.com.autocenterfiap.servico.application.dto.ServicoOutput;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarServicoUseCase - Testes Unitários")
class AtualizarServicoUseCaseTest {

    @Mock
    private ServicoRepositoryPort repositoryPort;

    @InjectMocks
    private AtualizarServicoUseCase atualizarServicoUseCase;

    @Test
    @DisplayName("Deve atualizar serviço com sucesso quando ID existir e dados forem válidos")
    void deveAtualizarServicoComSucesso() {
        Servico servicoExistente = Servico.builder()
                .id(1L)
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100.00))
                .build();

        AtualizarServicoInput input = AtualizarServicoInput.builder()
                .descricao("Troca de filtro")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(120.00))
                .build();

        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(servicoExistente));
        when(repositoryPort.salvar(any(Servico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServicoOutput output = atualizarServicoUseCase.executar(1L, input);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals("Troca de filtro", output.getDescricao());
        assertEquals(BigDecimal.valueOf(120.00), output.getValor());
        verify(repositoryPort, times(1)).buscarPorId(1L);
        verify(repositoryPort, times(1)).salvar(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar ServicoNaoEncontradoException ao atualizar serviço inexistente")
    void deveLancarServicoNaoEncontradoExceptionAoAtualizarInexistente() {
        AtualizarServicoInput input = AtualizarServicoInput.builder()
                .descricao("Troca de filtro")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(120.00))
                .build();

        when(repositoryPort.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(ServicoNaoEncontradoException.class, () -> atualizarServicoUseCase.executar(999L, input));
        verify(repositoryPort, times(1)).buscarPorId(999L);
        verify(repositoryPort, never()).salvar(any(Servico.class));
    }
}
