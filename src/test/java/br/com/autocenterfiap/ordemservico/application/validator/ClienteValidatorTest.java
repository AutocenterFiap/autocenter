package br.com.autocenterfiap.ordemservico.application.validator;

import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.application.dto.OrdemServico.CriarOrdemServicoInput;
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
@DisplayName("ClienteValidator - Testes Unitários")
class ClienteValidatorTest {

    @InjectMocks
    private ClienteValidator clienteValidator;

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Test
    void devePassarSemExcecaoQuandoClienteExistir() {
        Long clienteId = 1L;
        when(clienteRepositoryPort.existePorId(clienteId)).thenReturn(true);
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(1L, clienteId);
        Assertions.assertDoesNotThrow(() -> clienteValidator.validate(input));
    }

    @Test
    void deveRetornarExcecaoQuandoClienteNaoExistir() {
        Long clienteId = 999L;
        when(clienteRepositoryPort.existePorId(clienteId)).thenReturn(false);
        CriarOrdemServicoInput input = new CriarOrdemServicoInput(1L, clienteId);
        assertThrows(ClienteNaoEncontradoException.class, () -> clienteValidator.validate(input));
    }
}
