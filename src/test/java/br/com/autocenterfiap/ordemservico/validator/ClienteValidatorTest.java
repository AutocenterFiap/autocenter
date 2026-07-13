package br.com.autocenterfiap.ordemservico.validator;

import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.application.validator.ClienteValidator;
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
@DisplayName("Cliente Validator - Testes Unitários")
class ClienteValidatorTest {

    @InjectMocks
    private ClienteValidator clienteValidator;

    @Mock
    private ClienteRepository clienteRepository;

    @Test
    public void devePassarSemExcecaoQuandoClienteExistir() {
        Long clienteIdExistente = 1L;
        when(clienteRepository.existsById(clienteIdExistente)).thenReturn(true);
        OrdemServicoDTO dto = new OrdemServicoDTO(1L,clienteIdExistente);
        Assertions.assertDoesNotThrow(() -> clienteValidator.validate(dto));
    }

    @Test
    public void deveRetornarExcecaoQuandoClienteNaoExistir() {
        Long clienteIdInexistente = 999L;
        when(clienteRepository.existsById(clienteIdInexistente)).thenReturn(false);
        OrdemServicoDTO dto = new OrdemServicoDTO(1L,clienteIdInexistente);
        assertThrows(ClienteNaoEncontradoException.class, () -> {
            clienteValidator.validate(dto);
        });
    }
}