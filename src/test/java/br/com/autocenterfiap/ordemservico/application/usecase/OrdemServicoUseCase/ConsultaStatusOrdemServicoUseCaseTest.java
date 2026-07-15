package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.ConsultaStatusOrdemServicoOutput;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultaStatusOrdemServicoUseCase - Testes Unitários")
class ConsultaStatusOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private ConsultaStatusOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve retornar o status da ordem de serviço quando o id existir")
    void deveRetornarStatusQuandoIdExistir() {
        OrdemServico ordemServico = OrdemServico.builder()
                .id(1L)
                .statusOS(StatusOS.AGUARDANDO_APROVACAO)
                .build();

        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServico));

        ConsultaStatusOrdemServicoOutput output = useCase.executar(1L);

        assertNotNull(output);
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, output.statusOS());
    }

    @Test
    @DisplayName("Deve lançar OrdemServicoNaoEncontradaException quando o id não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        when(ordemServicoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(99L));
    }
}
