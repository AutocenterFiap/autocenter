package br.com.autocenterfiap.veiculo.application.usecase;

import br.com.autocenterfiap.veiculo.application.dto.AtualizarVeiculoInput;
import br.com.autocenterfiap.veiculo.application.dto.VeiculoOutput;
import br.com.autocenterfiap.veiculo.application.port.VeiculoRepositoryPort;
import br.com.autocenterfiap.veiculo.domain.entity.Veiculo;
import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarVeiculoUseCase - Testes Unitários")
class AtualizarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepositoryPort;

    @InjectMocks
    private AtualizarVeiculoUseCase atualizarVeiculoUseCase;

    private Veiculo veiculoExistente;
    private AtualizarVeiculoInput inputValido;

    @BeforeEach
    void setUp() {
        veiculoExistente = Veiculo.builder()
                .id(1L)
                .placa("ABC1D23")
                .chassi("9BWZZZ377VT004251")
                .renavam("82106426707")
                .marca("Ford")
                .modelo("Bronco")
                .anoFabricacao(2020)
                .anoModelo(2021)
                .cor("Preto")
                .quilometragem(45000L)
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        inputValido = AtualizarVeiculoInput.builder()
                .placa("XYZ9G99")
                .chassi("8BWZZZ377VT004251")
                .renavam("23022215548")
                .marca("Toyota")
                .modelo("Corolla")
                .anoFabricacao(2022)
                .anoModelo(2023)
                .cor("Branco")
                .quilometragem(50000L)
                .tipoCombustivel(TipoCombustivel.GASOLINA)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();
    }

    @Test
    @DisplayName("Deve atualizar veículo com sucesso quando dados forem válidos")
    void deveAtualizarVeiculoComSucesso() {
        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoRepositoryPort.existePorPlacaEIdDiferente("XYZ9G99", 1L)).thenReturn(false);
        when(veiculoRepositoryPort.existePorChassiEIdDiferente("8BWZZZ377VT004251", 1L)).thenReturn(false);
        when(veiculoRepositoryPort.existePorRenavamEIdDiferente("23022215548", 1L)).thenReturn(false);
        when(veiculoRepositoryPort.salvar(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VeiculoOutput output = atualizarVeiculoUseCase.executar(1L, inputValido);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals("XYZ9G99", output.getPlaca());
        assertEquals("Toyota", output.getMarca());
        assertEquals("Corolla", output.getModelo());
        assertEquals("Branco", output.getCor());
        verify(veiculoRepositoryPort, times(1)).salvar(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar VeiculoNaoEncontradoException quando veículo com ID não existir")
    void deveLancarVeiculoNaoEncontradoException() {
        when(veiculoRepositoryPort.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(VeiculoNaoEncontradoException.class, () -> atualizarVeiculoUseCase.executar(999L, inputValido));
    }

    @Test
    @DisplayName("Deve lançar PlacaJaCadastradaException quando placa pertencer a outro veículo")
    void deveLancarPlacaJaCadastradaException() {
        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoRepositoryPort.existePorPlacaEIdDiferente("XYZ9G99", 1L)).thenReturn(true);

        assertThrows(PlacaJaCadastradaException.class, () -> atualizarVeiculoUseCase.executar(1L, inputValido));
    }

    @Test
    @DisplayName("Deve lançar ChassiInvalidoException quando formato do chassi for inválido")
    void deveLancarChassiInvalidoException() {
        AtualizarVeiculoInput inputChassiInvalido = AtualizarVeiculoInput.builder()
                .placa("XYZ9G99")
                .chassi("123")
                .marca("Toyota")
                .modelo("Corolla")
                .tipoCombustivel(TipoCombustivel.GASOLINA)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoRepositoryPort.existePorPlacaEIdDiferente("XYZ9G99", 1L)).thenReturn(false);

        assertThrows(ChassiInvalidoException.class, () -> atualizarVeiculoUseCase.executar(1L, inputChassiInvalido));
    }

    @Test
    @DisplayName("Deve lançar ChassiJaCadastradoException quando chassi pertencer a outro veículo")
    void deveLancarChassiJaCadastradoException() {
        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoRepositoryPort.existePorPlacaEIdDiferente("XYZ9G99", 1L)).thenReturn(false);
        when(veiculoRepositoryPort.existePorChassiEIdDiferente("8BWZZZ377VT004251", 1L)).thenReturn(true);

        assertThrows(ChassiJaCadastradoException.class, () -> atualizarVeiculoUseCase.executar(1L, inputValido));
    }

    @Test
    @DisplayName("Deve lançar RenavamInvalidoException quando formato do renavam for inválido")
    void deveLancarRenavamInvalidoException() {
        AtualizarVeiculoInput inputRenavamInvalido = AtualizarVeiculoInput.builder()
                .placa("XYZ9G99")
                .chassi("8BWZZZ377VT004251")
                .renavam("11111111111")
                .marca("Toyota")
                .modelo("Corolla")
                .tipoCombustivel(TipoCombustivel.GASOLINA)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoRepositoryPort.existePorPlacaEIdDiferente("XYZ9G99", 1L)).thenReturn(false);
        when(veiculoRepositoryPort.existePorChassiEIdDiferente("8BWZZZ377VT004251", 1L)).thenReturn(false);

        assertThrows(RenavamInvalidoException.class, () -> atualizarVeiculoUseCase.executar(1L, inputRenavamInvalido));
    }

    @Test
    @DisplayName("Deve lançar RenavamJaCadastradoException quando renavam pertencer a outro veículo")
    void deveLancarRenavamJaCadastradoException() {
        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoRepositoryPort.existePorPlacaEIdDiferente("XYZ9G99", 1L)).thenReturn(false);
        when(veiculoRepositoryPort.existePorChassiEIdDiferente("8BWZZZ377VT004251", 1L)).thenReturn(false);
        when(veiculoRepositoryPort.existePorRenavamEIdDiferente("23022215548", 1L)).thenReturn(true);

        assertThrows(RenavamJaCadastradoException.class, () -> atualizarVeiculoUseCase.executar(1L, inputValido));
    }
}
