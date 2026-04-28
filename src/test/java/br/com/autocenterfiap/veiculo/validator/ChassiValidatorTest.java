package br.com.autocenterfiap.veiculo.validator;

import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.enums.TipoOperacao;
import br.com.autocenterfiap.veiculo.exception.ChassiInvalidoException;
import br.com.autocenterfiap.veiculo.exception.ChassiJaCadastradoException;
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
@DisplayName("Chassi Validator - Testes Unitários")
class ChassiValidatorTest {

    @InjectMocks
    private ChassiValidator chassiValidator;

    @Mock
    private VeiculoRepository veiculoRepository;

    private VeiculoValidationContext context;
    private VeiculoDTO veiculoDTO;
    private VeiculoDTO veiculoInvalidoDTO;
    private VeiculoDTO veiculoChassiVazioDTO;

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

        veiculoInvalidoDTO = new VeiculoDTO(
                "ABC1D23",
                "23",
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

        veiculoChassiVazioDTO = new VeiculoDTO(
                "ABC1D23",
                "",
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
    public void deveValidarChassiUnicoNaCriacao(){
        context = new VeiculoValidationContext(veiculoDTO, TipoOperacao.CREATE);
        when(veiculoRepository.existsByChassi(veiculoDTO.chassi())).thenReturn(false);
        assertDoesNotThrow(() -> chassiValidator.validate(context));
    }

    @Test
    public void deveIgnorarCasoChassiSejaVazio(){
        context = new VeiculoValidationContext(veiculoChassiVazioDTO, TipoOperacao.CREATE);
        assertDoesNotThrow(() -> chassiValidator.validate(context));
    }

    @Test
    public void deveLancarExcecaoCasoChassiSejaInvalido(){
        context = new VeiculoValidationContext(veiculoInvalidoDTO, TipoOperacao.CREATE);
        assertThrows(ChassiInvalidoException.class,() -> chassiValidator.validate(context));
    }

    @Test
    public void deveLancarExcecaoCasoChassiJaCadastradoNaCriacao(){
        context = new VeiculoValidationContext(veiculoDTO, TipoOperacao.CREATE);
        when(veiculoRepository.existsByChassi(veiculoDTO.chassi())).thenReturn(true);
        assertThrows(ChassiJaCadastradoException.class,() -> chassiValidator.validate(context));
    }

    @Test
    public void deveValidarChassiUnicoNaAtualizacao(){
        context = new VeiculoValidationContext(1L,veiculoDTO, TipoOperacao.UPDATE);
        when(veiculoRepository.existsByChassiAndIdNot(veiculoDTO.chassi(),1L)).thenReturn(false);
        assertDoesNotThrow(() -> chassiValidator.validate(context));
    }

    @Test
    public void deveLancarExcecaoCasoChassiJaCadastradoNaAtualizacao(){
        context = new VeiculoValidationContext(1L,veiculoDTO, TipoOperacao.UPDATE);
        when(veiculoRepository.existsByChassiAndIdNot(veiculoDTO.chassi(),1L)).thenReturn(true);
        assertThrows(ChassiJaCadastradoException.class,() -> chassiValidator.validate(context));
    }

}