package br.com.autocenterfiap.ordemservico.application.usecase.OSItemServicoUseCase;

import br.com.autocenterfiap.ordemservico.application.dto.OSItemServico.OSItemServicoOutput;
import br.com.autocenterfiap.ordemservico.application.dto.PageResult;
import br.com.autocenterfiap.ordemservico.application.dto.PaginationRequest;
import br.com.autocenterfiap.ordemservico.application.port.OSItemServicoRepositoryPort;
import br.com.autocenterfiap.ordemservico.domain.entity.OSItemServico;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarTodosPorOrdemServicoUseCase (Serviço) - Testes Unitários")
class ListarTodosPorOrdemServicoUseCaseTest {

    @Mock
    private OSItemServicoRepositoryPort itemServicoRepositoryPort;

    @InjectMocks
    private ListarTodosPorOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve listar os itens de serviço da ordem de serviço mapeados para output")
    void deveListarItensDaOrdemServico() {
        PaginationRequest pagination = new PaginationRequest(0, 10);
        OSItemServico item = OSItemServico.builder()
                .id(700L)
                .valorItemServico(BigDecimal.valueOf(120))
                .statusServico(StatusItemServico.AGUARDANDO_INICIO)
                .build();
        PageResult<OSItemServico> page = new PageResult<>(List.of(item), 0, 10, 1, 1);

        when(itemServicoRepositoryPort.findByOrdemServicoId(eq(1L), eq(pagination))).thenReturn(page);

        PageResult<OSItemServicoOutput> resultado = useCase.executar(1L, pagination);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(700L, resultado.getContent().get(0).id());
        assertEquals(StatusItemServico.AGUARDANDO_INICIO, resultado.getContent().get(0).statusItemServico());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando a ordem de serviço não tiver serviços")
    void deveRetornarPaginaVazia() {
        PaginationRequest pagination = new PaginationRequest(0, 10);
        PageResult<OSItemServico> page = new PageResult<>(Collections.emptyList(), 0, 10, 0, 0);

        when(itemServicoRepositoryPort.findByOrdemServicoId(eq(1L), eq(pagination))).thenReturn(page);

        PageResult<OSItemServicoOutput> resultado = useCase.executar(1L, pagination);

        assertTrue(resultado.getContent().isEmpty());
    }
}
