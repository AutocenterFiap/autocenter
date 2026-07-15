package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoNaoEncontradaException;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarOrdemServicoPorIdUseCase - Testes Unitários")
class BuscarOrdemServicoPorIdUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private BuscarOrdemServicoPorIdUseCase useCase;

    @Test
    @DisplayName("Deve retornar OrdemServicoOutput quando o id existir")
    void deveRetornarOrdemServicoQuandoIdExistir() {
        OrdemServico ordemServico = OrdemServico.builder()
                .id(1L)
                .numeroOrdemServico(1001L)
                .statusOS(StatusOS.ABERTA)
                .valorTotal(BigDecimal.valueOf(250))
                .veiculo(Veiculo.builder().id(5L).build())
                .cliente(Cliente.builder().id(7L).build())
                .build();

        when(ordemServicoRepositoryPort.findById(1L)).thenReturn(Optional.of(ordemServico));

        OrdemServicoOutput output = useCase.executar(1L);

        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals(1001L, output.numeroOrdemServico());
        assertEquals(StatusOS.ABERTA, output.statusOS());
        assertEquals(5L, output.veiculoId());
        assertEquals(7L, output.clienteId());
    }

    @Test
    @DisplayName("Deve lançar OrdemServicoNaoEncontradaException quando o id não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        when(ordemServicoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(99L));
    }
}
