package br.com.autocenterfiap.ordemservico.application.usecase.OrdemServicoUseCase;

import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.OrdemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarTodasOrdensServicosUseCase - Testes Unitários")
class ListarTodasOrdensServicosUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @InjectMocks
    private ListarTodasOrdensServicosUseCase useCase;

    @Test
    @DisplayName("Deve retornar página com as ordens de serviço mapeadas para output")
    void deveRetornarPaginaComOrdens() {
        PaginationRequest pagination = new PaginationRequest(0, 10);
        OrdemServico ordemServico = OrdemServico.builder()
                .id(1L)
                .statusOS(StatusOS.ABERTA)
                .valorTotal(BigDecimal.valueOf(150))
                .veiculo(Veiculo.builder().id(5L).build())
                .cliente(Cliente.builder().id(7L).build())
                .build();
        PageResult<OrdemServico> page = new PageResult<>(List.of(ordemServico), 0, 10, 1, 1);

        when(ordemServicoRepositoryPort.listarAtivasOrdenadasPorPrioridade(pagination)).thenReturn(page);

        PageResult<OrdemServicoOutput> resultado = useCase.executar(pagination);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1L, resultado.getContent().get(0).id());
        assertEquals(StatusOS.ABERTA, resultado.getContent().get(0).statusOS());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver ordens de serviço")
    void deveRetornarPaginaVazia() {
        PaginationRequest pagination = new PaginationRequest(0, 10);
        PageResult<OrdemServico> page = new PageResult<>(Collections.emptyList(), 0, 10, 0, 0);

        when(ordemServicoRepositoryPort.listarAtivasOrdenadasPorPrioridade(pagination)).thenReturn(page);

        PageResult<OrdemServicoOutput> resultado = useCase.executar(pagination);

        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getTotalElements());
    }
}
