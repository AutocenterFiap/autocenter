package br.com.autocenterfiap.ordemservico.validator;

import br.com.autocenterfiap.ordemservico.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.veiculo.exception.VeiculoNaoEncontradoException;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("Veiculo Validator - Testes Unitários")
class VeiculoValidatorTest {
    @InjectMocks
    private VeiculoValidator veiculoValidator;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Test
    public void devePassarSemExcecaoQuandoVeiculoExistir() {
        Long veiculoIdExistente = 1L;
        when(veiculoRepository.existsById(veiculoIdExistente)).thenReturn(true);
        OrdemServicoDTO dto = new OrdemServicoDTO(veiculoIdExistente,2L);
        Assertions.assertDoesNotThrow(() -> veiculoValidator.validate(dto));
    }

    @Test
    public void deveRetornarExcecaoQuandoVeiculoNaoExistir() {
        Long veiculoIdInexistente = 999L;
        when(veiculoRepository.existsById(veiculoIdInexistente)).thenReturn(false);
        OrdemServicoDTO dto = new OrdemServicoDTO(veiculoIdInexistente,1L);
        assertThrows(VeiculoNaoEncontradoException.class, () -> {
            veiculoValidator.validate(dto);
        });
    }
}