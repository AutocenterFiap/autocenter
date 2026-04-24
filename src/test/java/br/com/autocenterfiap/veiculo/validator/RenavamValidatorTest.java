package br.com.autocenterfiap.veiculo.validator;

import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.enums.TipoOperacao;
import br.com.autocenterfiap.veiculo.exception.ChassiInvalidoException;
import br.com.autocenterfiap.veiculo.exception.ChassiJaCadastradoException;
import br.com.autocenterfiap.veiculo.exception.RenavamInvalidoException;
import br.com.autocenterfiap.veiculo.exception.RenavamJaCadastradoException;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Renavam Validator - Testes Unitários")
class RenavamValidatorTest {
    @InjectMocks
    private RenavamValidator renavamValidator;

    @Mock
    private VeiculoRepository veiculoRepository;

    private VeiculoValidationContext context;
    private VeiculoDTO veiculoDTO;
    private VeiculoDTO veiculoInvalidoDTO;
    private VeiculoDTO veiculoRenavamVazioDTO;

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
                "9BWZZZ377VT004251",
                "11111111111",
                "Ford",
                "Bronco",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO
        );

        veiculoRenavamVazioDTO = new VeiculoDTO(
                "ABC1D23",
                "9BWZZZ377VT004251",
                "",
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
    public void deveValidarRenavamUnicoNaCriacao(){
        context = new VeiculoValidationContext(veiculoDTO, TipoOperacao.CREATE);
        when(veiculoRepository.existsByRenavam(veiculoDTO.renavam())).thenReturn(false);
        assertDoesNotThrow(() -> renavamValidator.validate(context));
    }

    @Test
    public void deveIgnorarCasoRenavamSejaVazio(){
        context = new VeiculoValidationContext(veiculoRenavamVazioDTO, TipoOperacao.CREATE);
        assertDoesNotThrow(() -> renavamValidator.validate(context));
    }

    @Test
    public void deveLancarExcecaoCasoRenavamSejaInvalido(){
        context = new VeiculoValidationContext(veiculoInvalidoDTO, TipoOperacao.CREATE);
        assertThrows(RenavamInvalidoException.class,() -> renavamValidator.validate(context));
    }

    @Test
    public void deveLancarExcecaoCasoRenavamJaCadastradoNaCriacao(){
        context = new VeiculoValidationContext(veiculoDTO, TipoOperacao.CREATE);
        when(veiculoRepository.existsByRenavam(veiculoDTO.renavam())).thenReturn(true);
        assertThrows(RenavamJaCadastradoException.class,() -> renavamValidator.validate(context));
    }

    @Test
    public void deveValidarRenavamUnicoNaAtualizacao(){
        context = new VeiculoValidationContext(1L,veiculoDTO, TipoOperacao.UPDATE);
        when(veiculoRepository.existsByRenavamAndIdNot(veiculoDTO.renavam(),1L)).thenReturn(false);
        assertDoesNotThrow(() -> renavamValidator.validate(context));
    }

    @Test
    public void deveLancarExcecaoCasoRenavamJaCadastradoNaAtualizacao(){
        context = new VeiculoValidationContext(1L,veiculoDTO, TipoOperacao.UPDATE);
        when(veiculoRepository.existsByRenavamAndIdNot(veiculoDTO.renavam(),1L)).thenReturn(true);
        assertThrows(RenavamJaCadastradoException.class,() -> renavamValidator.validate(context));
    }
}