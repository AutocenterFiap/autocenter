package br.com.autocenterfiap.veiculo.application.usecase;

import br.com.autocenterfiap.veiculo.application.dto.CriarVeiculoInput;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarVeiculoUseCase - Testes Unitários")
class CriarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepositoryPort;

    @InjectMocks
    private CriarVeiculoUseCase criarVeiculoUseCase;

    private CriarVeiculoInput inputValido;

    @BeforeEach
    void setUp() {
        inputValido = CriarVeiculoInput.builder()
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
    }

    @Test
    @DisplayName("Deve criar veículo com sucesso quando todos os dados forem válidos")
    void deveCriarVeiculoComSucesso() {
        when(veiculoRepositoryPort.existePorPlaca("ABC1D23")).thenReturn(false);
        when(veiculoRepositoryPort.existePorChassi("9BWZZZ377VT004251")).thenReturn(false);
        when(veiculoRepositoryPort.existePorRenavam("82106426707")).thenReturn(false);
        when(veiculoRepositoryPort.salvar(any(Veiculo.class))).thenAnswer(invocation -> {
            Veiculo v = invocation.getArgument(0);
            v.setId(1L);
            return v;
        });

        VeiculoOutput output = criarVeiculoUseCase.executar(inputValido);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals("ABC1D23", output.getPlaca());
        assertEquals("Ford", output.getMarca());
        assertEquals("Bronco", output.getModelo());
        verify(veiculoRepositoryPort, times(1)).salvar(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando campos obrigatórios do domínio forem nulos/vazios")
    void deveLancarExcecaoQuandoCamposObrigatoriosForemInvalidos() {
        // Placa vazia
        CriarVeiculoInput inputPlacaVazia = CriarVeiculoInput.builder()
                .placa("")
                .marca("Ford")
                .modelo("Bronco")
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        assertThrows(IllegalArgumentException.class, () -> criarVeiculoUseCase.executar(inputPlacaVazia));

        // Marca nula
        CriarVeiculoInput inputMarcaNula = CriarVeiculoInput.builder()
                .placa("ABC1D23")
                .marca(null)
                .modelo("Bronco")
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        assertThrows(IllegalArgumentException.class, () -> criarVeiculoUseCase.executar(inputMarcaNula));
    }

    @Test
    @DisplayName("Deve lançar ChassiInvalidoException quando formato do chassi for inválido")
    void deveLancarChassiInvalidoException() {
        CriarVeiculoInput inputChassiInvalido = CriarVeiculoInput.builder()
                .placa("ABC1D23")
                .chassi("123") // Chassi curto demais
                .renavam("82106426707")
                .marca("Ford")
                .modelo("Bronco")
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        assertThrows(ChassiInvalidoException.class, () -> criarVeiculoUseCase.executar(inputChassiInvalido));
    }

    @Test
    @DisplayName("Deve ignorar validação de chassi quando chassi for nulo ou em branco")
    void deveIgnorarValidacaoChassiQuandoNuloOuEmBranco() {
        CriarVeiculoInput inputChassiNull = CriarVeiculoInput.builder()
                .placa("ABC1D23")
                .chassi(null)
                .renavam("82106426707")
                .marca("Ford")
                .modelo("Bronco")
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        when(veiculoRepositoryPort.existePorPlaca("ABC1D23")).thenReturn(false);
        when(veiculoRepositoryPort.existePorRenavam("82106426707")).thenReturn(false);
        when(veiculoRepositoryPort.salvar(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> criarVeiculoUseCase.executar(inputChassiNull));
    }

    @Test
    @DisplayName("Deve lançar ChassiJaCadastradoException quando chassi já existir")
    void deveLancarChassiJaCadastradoException() {
        when(veiculoRepositoryPort.existePorChassi("9BWZZZ377VT004251")).thenReturn(true);

        assertThrows(ChassiJaCadastradoException.class, () -> criarVeiculoUseCase.executar(inputValido));
    }

    @Test
    @DisplayName("Deve lançar RenavamInvalidoException quando formato do renavam for inválido")
    void deveLancarRenavamInvalidoException() {
        CriarVeiculoInput inputRenavamInvalido = CriarVeiculoInput.builder()
                .placa("ABC1D23")
                .chassi("9BWZZZ377VT004251")
                .renavam("11111111111") // Renavam inválido pelo cálculo de dígito
                .marca("Ford")
                .modelo("Bronco")
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        assertThrows(RenavamInvalidoException.class, () -> criarVeiculoUseCase.executar(inputRenavamInvalido));
    }

    @Test
    @DisplayName("Deve ignorar validação de renavam quando for nulo ou em branco")
    void deveIgnorarValidacaoRenavamQuandoNuloOuEmBranco() {
        CriarVeiculoInput inputRenavamNull = CriarVeiculoInput.builder()
                .placa("ABC1D23")
                .chassi("9BWZZZ377VT004251")
                .renavam(null)
                .marca("Ford")
                .modelo("Bronco")
                .tipoCombustivel(TipoCombustivel.DIESEL)
                .categoriaVeiculo(CategoriaVeiculo.CARRO)
                .build();

        when(veiculoRepositoryPort.existePorPlaca("ABC1D23")).thenReturn(false);
        when(veiculoRepositoryPort.existePorChassi("9BWZZZ377VT004251")).thenReturn(false);
        when(veiculoRepositoryPort.salvar(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> criarVeiculoUseCase.executar(inputRenavamNull));
    }

    @Test
    @DisplayName("Deve lançar RenavamJaCadastradoException quando renavam já existir")
    void deveLancarRenavamJaCadastradoException() {
        when(veiculoRepositoryPort.existePorChassi("9BWZZZ377VT004251")).thenReturn(false);
        when(veiculoRepositoryPort.existePorRenavam("82106426707")).thenReturn(true);

        assertThrows(RenavamJaCadastradoException.class, () -> criarVeiculoUseCase.executar(inputValido));
    }

    @Test
    @DisplayName("Deve lançar PlacaJaCadastradaException quando placa já existir")
    void deveLancarPlacaJaCadastradaException() {
        when(veiculoRepositoryPort.existePorChassi("9BWZZZ377VT004251")).thenReturn(false);
        when(veiculoRepositoryPort.existePorRenavam("82106426707")).thenReturn(false);
        when(veiculoRepositoryPort.existePorPlaca("ABC1D23")).thenReturn(true);

        assertThrows(PlacaJaCadastradaException.class, () -> criarVeiculoUseCase.executar(inputValido));
    }
}
