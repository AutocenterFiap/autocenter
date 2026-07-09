package br.com.autocenterfiap.servico.application.usecase;

import br.com.autocenterfiap.servico.application.dto.PageResult;
import br.com.autocenterfiap.servico.application.dto.PaginationRequest;
import br.com.autocenterfiap.servico.application.dto.ServicoOutput;
import br.com.autocenterfiap.servico.application.port.ServicoRepositoryPort;
import br.com.autocenterfiap.servico.domain.entity.Servico;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarServicosPorStatusUseCase - Testes Unitários")
class ListarServicosPorStatusUseCaseTest {

    @Mock
    private ServicoRepositoryPort repositoryPort;

    @InjectMocks
    private ListarServicosPorStatusUseCase listarServicosPorStatusUseCase;

    @Test
    @DisplayName("Deve listar serviços por status com paginação")
    void deveListarServicosPorStatusComPaginacao() {
        PaginationRequest pagination = new PaginationRequest(0, 10, "descricao", "ASC");

        Servico servico = Servico.builder()
                .id(1L)
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100.00))
                .build();

        PageResult<Servico> pageResult = new PageResult<>(List.of(servico), 0, 10, 1L);

        when(repositoryPort.listarPorStatus(StatusServico.ATIVO, pagination)).thenReturn(pageResult);

        PageResult<ServicoOutput> output = listarServicosPorStatusUseCase.executar(StatusServico.ATIVO, pagination);

        assertNotNull(output);
        assertEquals(1, output.getContent().size());
        assertEquals(StatusServico.ATIVO, output.getContent().get(0).getStatus());
        verify(repositoryPort, times(1)).listarPorStatus(StatusServico.ATIVO, pagination);
    }
}
