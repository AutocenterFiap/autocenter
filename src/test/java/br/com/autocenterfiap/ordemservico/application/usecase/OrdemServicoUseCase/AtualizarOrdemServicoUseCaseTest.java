package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.AtualizarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
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
@DisplayName("AtualizarOrdemServicoUseCase - Testes Unitários")
class AtualizarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private AtualizarOrdemServicoUseCase useCase;

    private OrdemServico ordemServicoAberta() {
        return OrdemServico.builder()
                .id(1L)
                .statusOS(StatusOS.ABERTA)
                .veiculo(Veiculo.builder().id(5L).build())
                .cliente(Cliente.builder().id(7L).build())
                .build();
    }

    @Test
    @DisplayName("Deve atualizar o status quando a transição for válida")
    void deveAtualizarStatusComTransicaoValida() {
        OrdemServico ordemServico = ordemServicoAberta();
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepositoryPort.save(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoOutput output = useCase.executar(1L, new AtualizarOrdemServicoInput(StatusOS.RECEBIDA));

        assertEquals(StatusOS.RECEBIDA, output.statusOS());
        verify(ordemServicoRepositoryPort).save(ordemServico);
    }

    @Test
    @DisplayName("Deve lançar StatusOSInvalidoException quando a transição for inválida")
    void deveLancarExcecaoComTransicaoInvalida() {
        OrdemServico ordemServico = ordemServicoAberta();
        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServico));

        assertThrows(StatusOSInvalidoException.class,
                () -> useCase.executar(1L, new AtualizarOrdemServicoInput(StatusOS.APROVADA)));
        verify(ordemServicoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar OrdemServicoNaoEncontradaException quando o id não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        when(ordemServicoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class,
                () -> useCase.executar(99L, new AtualizarOrdemServicoInput(StatusOS.RECEBIDA)));
        verify(ordemServicoRepositoryPort, never()).save(any());
    }
}
