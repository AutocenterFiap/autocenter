package br.com.autocenterfiap.ordemservico.application.validator;

import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
import br.com.autocenterfiap.ordemservico.application.exception.OrdemServicoJaAbertaParaVeiculoException;
import br.com.autocenterfiap.ordemservico.application.port.OrdemServicoRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrdemServicoDuplicadaValidator - Testes Unitários")
class OrdemServicoDuplicadaValidatorTest {

    @InjectMocks
    private OrdemServicoDuplicadaValidator ordemServicoDuplicadaValidator;

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @Test
    void devePassarQuandoNaoExistirOrdemServicoAbertaParaVeiculo() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(1L, 1L, List.of(), java.util.Map.of());
        doReturn(false).when(ordemServicoRepositoryPort).existsByVeiculoIdAndStatusOSIn(eq(1L), anyList());
        assertDoesNotThrow(() -> ordemServicoDuplicadaValidator.validate(input));
    }

    @Test
    void deveLancarExceptionQuandoExistirOrdemServicoAbertaParaVeiculo() {
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(1L, 1L, List.of(), java.util.Map.of());
        doReturn(true).when(ordemServicoRepositoryPort).existsByVeiculoIdAndStatusOSIn(eq(1L), anyList());
        assertThrows(OrdemServicoJaAbertaParaVeiculoException.class,
                () -> ordemServicoDuplicadaValidator.validate(input));
    }
}
