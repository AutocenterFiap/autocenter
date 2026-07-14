package br.com.autocenterfiap.veiculo.application.usecase;

import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarVeiculoPorPlacaUseCase - Testes Unitários")
class BuscarVeiculoPorPlacaUseCaseTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepositoryPort;

    @InjectMocks
    private BuscarVeiculoPorPlacaUseCase buscarVeiculoPorPlacaUseCase;

    private Veiculo veiculoExistente;

    @BeforeEach
    void setUp() {
        veiculoExistente = Veiculo.builder()
                .id(1L)
                .placa("ABC1D23")
                .marca("Ford")
                .modelo("Bronco")
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();
    }

    @Test
    @DisplayName("Deve retornar VeiculoOutput quando Placa existir")
    void deveRetornarVeiculoQuandoPlacaExistir() {
        when(veiculoRepositoryPort.buscarPorPlaca("ABC1D23")).thenReturn(Optional.of(veiculoExistente));

        VeiculoOutput output = buscarVeiculoPorPlacaUseCase.executar("ABC1D23");

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals("ABC1D23", output.getPlaca());
        verify(veiculoRepositoryPort, times(1)).buscarPorPlaca("ABC1D23");
    }

    @Test
    @DisplayName("Deve lançar VeiculoNaoEncontradoException quando Placa não existir")
    void deveLancarVeiculoNaoEncontradoExceptionQuandoPlacaNaoExistir() {
        when(veiculoRepositoryPort.buscarPorPlaca("XYZ9X99")).thenReturn(Optional.empty());

        assertThrows(VeiculoNaoEncontradoException.class, () -> buscarVeiculoPorPlacaUseCase.executar("XYZ9X99"));
        verify(veiculoRepositoryPort, times(1)).buscarPorPlaca("XYZ9X99");
    }
}
