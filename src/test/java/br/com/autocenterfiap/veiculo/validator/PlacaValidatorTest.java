package br.com.autocenterfiap.veiculo.validator;

import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.enums.TipoOperacao;
import br.com.autocenterfiap.veiculo.exception.PlacaJaCadastradaException;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Placa Validator - Testes Unitários")
class PlacaValidatorTest {
    @InjectMocks
    private PlacaValidator placaValidator;

    @Mock
    private VeiculoRepository veiculoRepository;

    private VeiculoValidationContext context;
    private VeiculoDTO veiculoDTO;

    @BeforeEach
    public void setUp(){
        veiculoDTO = new VeiculoDTO(
                "ABC1D23",
                "9BWZZZ377VT004251",
                "82106426707",
                "Ford",
                "Bronco",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO
        );
    }

    @Test
    public void deveValidarPlacaUnicaNaCriacao(){
        context = new VeiculoValidationContext(veiculoDTO, TipoOperacao.CREATE);
        when(veiculoRepository.existsByPlaca("ABC1D23")).thenReturn(false);
        assertDoesNotThrow(() -> placaValidator.validate(context));
    }

    @Test
    public void deveLancarExcecaoCasoPlacaJaEstejaCadastradaNaCriacao(){
        context = new VeiculoValidationContext(veiculoDTO, TipoOperacao.CREATE);
        when(veiculoRepository.existsByPlaca("ABC1D23")).thenReturn(true);
        assertThrows(PlacaJaCadastradaException.class,() -> placaValidator.validate(context));
    }

    @Test
    public void deveValidarPlacaUnicaNaAtualizacao(){
        context = new VeiculoValidationContext(1L,veiculoDTO, TipoOperacao.UPDATE);
        when(veiculoRepository.existsByPlacaAndIdNot("ABC1D23",1L)).thenReturn(false);
        assertDoesNotThrow(() -> placaValidator.validate(context));
    }

    @Test
    public void deveLancarExcecaoCasoPlacaJaEstejaCadastradaNaAtualizacao(){
        context = new VeiculoValidationContext(1L,veiculoDTO, TipoOperacao.UPDATE);
        when(veiculoRepository.existsByPlacaAndIdNot("ABC1D23",1L)).thenReturn(true);
        assertThrows(PlacaJaCadastradaException.class,() -> placaValidator.validate(context));
    }

}