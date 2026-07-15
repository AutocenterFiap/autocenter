package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OrdemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeletarOrdemServicoUseCase - Testes Unitários")
class DeletarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private DeletarOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve deletar a ordem de serviço quando o id existir")
    void deveDeletarQuandoIdExistir() {
        OrdemServico ordemServico = OrdemServico.builder()
                .id(1L)
                .statusOS(StatusOS.ABERTA)
                .build();

        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServico));

        useCase.executar(1L);

        verify(ordemServicoRepositoryPort).delete(ordemServico);
    }

    @Test
    @DisplayName("Deve lançar OrdemServicoNaoEncontradaException quando o id não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        when(ordemServicoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(99L));
        verify(ordemServicoRepositoryPort, never()).delete(any());
    }
}
