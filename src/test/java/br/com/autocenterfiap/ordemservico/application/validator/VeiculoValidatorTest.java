package br.com.autocenterfiap.ordemservico.application.validator;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.veiculo.domain.exception.VeiculoNaoEncontradoException;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.repository.VeiculoJpaRepository;
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
@DisplayName("VeiculoValidator - Testes Unitários")
class VeiculoValidatorTest {

    @InjectMocks
    private VeiculoValidator veiculoValidator;

    @Mock
    private VeiculoJpaRepository veiculoRepository;

    @Test
    void devePassarSemExcecaoQuandoVeiculoExistir() {
        Long veiculoId = 1L;
        when(veiculoRepository.existsById(veiculoId)).thenReturn(true);
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(veiculoId, 2L);
        Assertions.assertDoesNotThrow(() -> veiculoValidator.validate(input));
    }

    @Test
    void deveRetornarExcecaoQuandoVeiculoNaoExistir() {
        Long veiculoId = 999L;
        when(veiculoRepository.existsById(veiculoId)).thenReturn(false);
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(veiculoId, 1L);
        assertThrows(VeiculoNaoEncontradoException.class, () -> veiculoValidator.validate(input));
    }
}
