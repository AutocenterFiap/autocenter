package br.com.autocenterfiap.veiculo.application.usecase;

import br.com.autocenterfiap.veiculo.application.dto.PageResult;
import br.com.autocenterfiap.veiculo.application.dto.PaginationRequest;
import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarVeiculosUseCase - Testes Unitários")
class ListarVeiculosUseCaseTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepositoryPort;

    @InjectMocks
    private ListarVeiculosUseCase listarVeiculosUseCase;

    private PaginationRequest paginationRequest;
    private PageResult<Veiculo> pageResult;

    @BeforeEach
    void setUp() {
        paginationRequest = new PaginationRequest(0, 10);
        Veiculo veiculo = Veiculo.builder()
                .id(1L)
                .placa("ABC1D23")
                .marca("Ford")
                .modelo("Bronco")
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();
        pageResult = new PageResult<>(List.of(veiculo), 0, 10, 1L);
    }

    @Test
    @DisplayName("Deve retornar PageResult com VeiculoOutput mapeados")
    void deveListarVeiculosComSucesso() {
        when(veiculoRepositoryPort.listarTodos(paginationRequest)).thenReturn(pageResult);

        PageResult<VeiculoOutput> result = listarVeiculosUseCase.executar(paginationRequest);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("ABC1D23", result.getContent().get(0).getPlaca());
        verify(veiculoRepositoryPort, times(1)).listarTodos(paginationRequest);
    }
}
