package br.com.autocenterfiap.cliente.application.usecase;

import br.com.autocenterfiap.cliente.application.dto.CriarClienteInput;
import br.com.autocenterfiap.cliente.application.dto.ClienteOutput;
import br.com.autocenterfiap.cliente.application.dto.EnderecoInput;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.enums.TipoCliente;
import br.com.autocenterfiap.cliente.domain.exception.ClienteDocumentoInvalidoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteDocumentoJaCadastradoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteEmailJaCadastradoException;
import br.com.autocenterfiap.cliente.domain.service.ValidadorDocumento;
import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.application.port.ValidadorDocumentoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Teste - Criar Cliente Use Case")
class CriarClienteUseCaseTest {

    private CriarClienteUseCase criarClienteUseCase;
    private ClienteRepositoryPort mockRepository;
    private ValidadorDocumentoPort mockValidadorPort;
    private ValidadorDocumento mockValidadorCpf;

    @BeforeEach
    void setup() {
        mockRepository = mock(ClienteRepositoryPort.class);
        mockValidadorPort = mock(ValidadorDocumentoPort.class);
        mockValidadorCpf = mock(ValidadorDocumento.class);

        criarClienteUseCase = new CriarClienteUseCase(mockRepository, mockValidadorPort);
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso quando todos os dados são válidos")
    void deveCriarClienteComSucesso() {
        var input = CriarClienteInput.builder()
            .nome("João da Silva")
            .tipoCliente(TipoCliente.PESSOA_FISICA)
            .documento("11144477735")
            .email("joao.silva@email.com")
            .telefone("11987654321")
            .endereco(EnderecoInput.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .numero("100")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .estado("SP")
                .build())
            .build();

        when(mockValidadorPort.obterValidador("PESSOA_FISICA"))
            .thenReturn(mockValidadorCpf);
        when(mockValidadorCpf.validar("11144477735"))
            .thenReturn(true);

        when(mockRepository.existePorDocumento("11144477735"))
            .thenReturn(false);
        when(mockRepository.existePorEmail("joao.silva@email.com"))
            .thenReturn(false);

        var clienteSalvo = Cliente.builder()
            .id(1L)
            .nome("João da Silva")
            .tipoCliente(TipoCliente.PESSOA_FISICA)
            .documento("11144477735")
            .email("joao.silva@email.com")
            .telefone("11987654321")
            .build();
        when(mockRepository.salvar(any(Cliente.class)))
            .thenReturn(clienteSalvo);

        ClienteOutput output = criarClienteUseCase.executar(input);

        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals("João da Silva", output.getNome());
        assertEquals("11144477735", output.getDocumento());
        assertEquals(TipoCliente.PESSOA_FISICA, output.getTipoCliente());

        verify(mockRepository, times(1)).salvar(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando documento é inválido")
    void deveLançarExcecaoQuandoDocumentoInvalido() {
        var input = CriarClienteInput.builder()
            .nome("João da Silva")
            .tipoCliente(TipoCliente.PESSOA_FISICA)
            .documento("12345678901")
            .email("joao@email.com")
            .telefone("11987654321")
            .endereco(EnderecoInput.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .cidade("São Paulo")
                .estado("SP")
                .build())
            .build();

        when(mockValidadorPort.obterValidador("PESSOA_FISICA"))
            .thenReturn(mockValidadorCpf);
        when(mockValidadorCpf.validar("12345678901"))
            .thenReturn(false);

        assertThrows(ClienteDocumentoInvalidoException.class, () -> {
            criarClienteUseCase.executar(input);
        });

        verify(mockRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando documento já está cadastrado")
    void deveLançarExcecaoQuandoDocumentoDuplicado() {
        var input = CriarClienteInput.builder()
            .nome("João da Silva")
            .tipoCliente(TipoCliente.PESSOA_FISICA)
            .documento("11144477735")
            .email("joao@email.com")
            .telefone("11987654321")
            .endereco(EnderecoInput.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .cidade("São Paulo")
                .estado("SP")
                .build())
            .build();

        when(mockValidadorPort.obterValidador("PESSOA_FISICA"))
            .thenReturn(mockValidadorCpf);
        when(mockValidadorCpf.validar("11144477735"))
            .thenReturn(true);

        when(mockRepository.existePorDocumento("11144477735"))
            .thenReturn(true);

        assertThrows(ClienteDocumentoJaCadastradoException.class, () -> {
            criarClienteUseCase.executar(input);
        });

        verify(mockRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está cadastrado")
    void deveLançarExcecaoQuandoEmailDuplicado() {
        var input = CriarClienteInput.builder()
            .nome("João da Silva")
            .tipoCliente(TipoCliente.PESSOA_FISICA)
            .documento("11144477735")
            .email("joao@email.com")
            .telefone("11987654321")
            .endereco(EnderecoInput.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .cidade("São Paulo")
                .estado("SP")
                .build())
            .build();

        when(mockValidadorPort.obterValidador("PESSOA_FISICA"))
            .thenReturn(mockValidadorCpf);
        when(mockValidadorCpf.validar("11144477735"))
            .thenReturn(true);

        when(mockRepository.existePorDocumento("11144477735"))
            .thenReturn(false);
        when(mockRepository.existePorEmail("joao@email.com"))
            .thenReturn(true);

        assertThrows(ClienteEmailJaCadastradoException.class, () -> {
            criarClienteUseCase.executar(input);
        });

        verify(mockRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando endereço é inválido")
    void deveLançarExcecaoQuandoEnderecoInvalido() {
        var input = CriarClienteInput.builder()
            .nome("João da Silva")
            .tipoCliente(TipoCliente.PESSOA_FISICA)
            .documento("11144477735")
            .email("joao@email.com")
            .telefone("11987654321")
            .endereco(EnderecoInput.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .cidade("")
                .estado("SP")
                .build())
            .build();

        assertThrows(IllegalArgumentException.class, () -> {
            criarClienteUseCase.executar(input);
        });

        verify(mockRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve validar tipo de cliente conforme o tamanho do documento")
    void deveValidarTamanhoDoCpf() {
        var input = CriarClienteInput.builder()
            .nome("João")
            .tipoCliente(TipoCliente.PESSOA_FISICA)
            .documento("111444777350")
            .email("joao@email.com")
            .telefone("11987654321")
            .endereco(EnderecoInput.builder()
                .cep("01310100")
                .logradouro("Rua A")
                .cidade("São Paulo")
                .estado("SP")
                .build())
            .build();

        assertThrows(IllegalArgumentException.class, () -> {
            criarClienteUseCase.executar(input);
        });

        verify(mockRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve chamar validador com tipo correto de cliente")
    void deveObtterValidadorCpfParaPessoaFisica() {
        var input = CriarClienteInput.builder()
            .nome("João")
            .tipoCliente(TipoCliente.PESSOA_FISICA)
            .documento("11144477735")
            .email("joao@email.com")
            .telefone("11987654321")
            .endereco(EnderecoInput.builder()
                .cep("01310100")
                .logradouro("Rua A")
                .cidade("São Paulo")
                .estado("SP")
                .build())
            .build();

        when(mockValidadorPort.obterValidador("PESSOA_FISICA"))
            .thenReturn(mockValidadorCpf);
        when(mockValidadorCpf.validar("11144477735"))
            .thenReturn(true);
        when(mockRepository.existePorDocumento(any()))
            .thenReturn(false);
        when(mockRepository.existePorEmail(any()))
            .thenReturn(false);
        when(mockRepository.salvar(any()))
            .thenReturn(Cliente.builder().id(1L).build());

        criarClienteUseCase.executar(input);

        verify(mockValidadorPort, times(1))
            .obterValidador("PESSOA_FISICA");
    }
}
