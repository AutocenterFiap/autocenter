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
@DisplayName("BuscarOrdemServicoPorNumeroUseCase - Testes Unitários")
class BuscarOrdemServicoPorNumeroUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private BuscarOrdemServicoPorNumeroUseCase useCase;

    @Test
    @DisplayName("Deve retornar OrdemServicoOutput quando o número existir")
    void deveRetornarOrdemServicoQuandoNumeroExistir() {
        OrdemServico ordemServico = OrdemServico.builder()
                .id(1L)
                .numeroOrdemServico(2002L)
                .statusOS(StatusOS.EM_EXECUCAO)
                .valorTotal(BigDecimal.valueOf(300))
                .veiculo(Veiculo.builder().id(5L).build())
                .cliente(Cliente.builder().id(7L).build())
                .build();

        when(ordemServicoRepositoryPort.findByNumeroOrdemServico(2002L)).thenReturn(Optional.of(ordemServico));

        OrdemServicoOutput output = useCase.executar(2002L);

        assertNotNull(output);
        assertEquals(2002L, output.numeroOrdemServico());
        assertEquals(StatusOS.EM_EXECUCAO, output.statusOS());
    }

    @Test
    @DisplayName("Deve lançar OrdemServicoNaoEncontradaException quando o número não existir")
    void deveLancarExcecaoQuandoNumeroNaoExistir() {
        when(ordemServicoRepositoryPort.findByNumeroOrdemServico(9999L)).thenReturn(Optional.empty());

        assertThrows(OrdemServicoNaoEncontradaException.class, () -> useCase.executar(9999L));
    }
}
