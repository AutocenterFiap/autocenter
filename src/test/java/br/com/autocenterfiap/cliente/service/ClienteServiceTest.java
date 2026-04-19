package br.com.autocenterfiap.cliente.service;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoInvalidoException;
import br.com.autocenterfiap.cliente.exception.DocumentoJaCadastradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoNaoPodeSerAlteradoException;
import br.com.autocenterfiap.cliente.exception.EmailJaCadastradoException;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.model.Endereco;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Testes Unitários")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clientePF;
    private Cliente clientePJ;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        // Setup Endereço
        endereco = new Endereco(
                "01310100",
                "Avenida Paulista",
                "1578",
                "Andar 5",
                "Bela Vista",
                "São Paulo",
                "SP"
        );

        // Setup Cliente Pessoa Física
        clientePF = new Cliente();
        clientePF.setId(1L);
        clientePF.setNome("João da Silva");
        clientePF.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clientePF.setDocumento("11144477735");
        clientePF.setEmail("joao.silva@email.com");
        clientePF.setTelefone("11987654321");
        clientePF.setEndereco(endereco);
        clientePF.setDataNascimento(LocalDate.of(1990, 5, 15));

        // Setup Cliente Pessoa Jurídica
        clientePJ = new Cliente();
        clientePJ.setId(2L);
        clientePJ.setNome("Empresa ABC LTDA");
        clientePJ.setTipoCliente(TipoCliente.PESSOA_JURIDICA);
        clientePJ.setDocumento("11222333000181");
        clientePJ.setEmail("contato@empresaabc.com");
        clientePJ.setTelefone("1133334444");
        clientePJ.setEndereco(endereco);
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarTodosOsClientes() {
        List<Cliente> clientesEsperados = Arrays.asList(clientePF, clientePJ);
        when(clienteRepository.findAll()).thenReturn(clientesEsperados);

        List<Cliente> resultado = clienteService.listarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of());

        List<Cliente> resultado = clienteService.listarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarClientePorIdComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));

        Optional<Cliente> resultado = clienteService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("João da Silva", resultado.get().getNome());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarOptionalVazioQuandoClienteNaoExiste() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.buscarPorId(999L);

        assertFalse(resultado.isPresent());
        verify(clienteRepository, times(1)).findById(999L);
    }

    @Test
    void deveBuscarClientePorDocumentoComSucesso() {
        when(clienteRepository.findByDocumento("11144477735")).thenReturn(Optional.of(clientePF));

        Optional<Cliente> resultado = clienteService.buscarPorDocumento("11144477735");

        assertTrue(resultado.isPresent());
        assertEquals("João da Silva", resultado.get().getNome());
        verify(clienteRepository, times(1)).findByDocumento("11144477735");
    }

    @Test
    void deveBuscarClientePorEmailComSucesso() {
        when(clienteRepository.findByEmail("joao.silva@email.com")).thenReturn(Optional.of(clientePF));

        Optional<Cliente> resultado = clienteService.buscarPorEmail("joao.silva@email.com");

        assertTrue(resultado.isPresent());
        assertEquals("João da Silva", resultado.get().getNome());
        verify(clienteRepository, times(1)).findByEmail("joao.silva@email.com");
    }

    @Test
    void deveCriarClientePessoaFisicaComSucesso() {
        clientePF.setId(null);
        when(clienteRepository.existsByDocumento(anyString())).thenReturn(false);
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clientePF);

        Cliente resultado = clienteService.criar(clientePF);

        assertNotNull(resultado);
        assertEquals("João da Silva", resultado.getNome());
        assertEquals(TipoCliente.PESSOA_FISICA, resultado.getTipoCliente());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveCriarClientePessoaJuridicaComSucesso() {
        clientePJ.setId(null);
        when(clienteRepository.existsByDocumento(anyString())).thenReturn(false);
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clientePJ);

        Cliente resultado = clienteService.criar(clientePJ);

        assertNotNull(resultado);
        assertEquals("Empresa ABC LTDA", resultado.getNome());
        assertEquals(TipoCliente.PESSOA_JURIDICA, resultado.getTipoCliente());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com documento já cadastrado")
    void deveLancarExcecaoAoCriarClienteComDocumentoJaCadastrado() {
        when(clienteRepository.existsByDocumento("11144477735")).thenReturn(true);

        assertThrows(DocumentoJaCadastradoException.class, () -> clienteService.criar(clientePF));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClienteComEmailJaCadastrado() {
        when(clienteRepository.existsByDocumento(anyString())).thenReturn(false);
        when(clienteRepository.existsByEmail("joao.silva@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> clienteService.criar(clientePF));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClientePFComCpfInvalido() {
        clientePF.setDocumento("12345678901"); // CPF inválido

        assertThrows(DocumentoInvalidoException.class, () -> clienteService.criar(clientePF));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClientePJComCnpjInvalido() {
        clientePJ.setDocumento("12345678901234"); // CNPJ inválido

        assertThrows(DocumentoInvalidoException.class, () -> clienteService.criar(clientePJ));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClientePFComCnpj() {
        clientePF.setDocumento("11222333000181"); // CNPJ com tipo PF

        assertThrows(IllegalArgumentException.class, () -> clienteService.criar(clientePF));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClientePJComCpf() {
        clientePJ.setDocumento("11144477735"); // CPF com tipo PJ

        assertThrows(IllegalArgumentException.class, () -> clienteService.criar(clientePJ));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João da Silva Updated");
        clienteAtualizado.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteAtualizado.setDocumento("11144477735"); // mesmo documento
        clienteAtualizado.setEmail("joao.novo@email.com");
        clienteAtualizado.setTelefone("11999998888");
        clienteAtualizado.setEndereco(endereco);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));
        when(clienteRepository.existsByEmail("joao.novo@email.com")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);

        Cliente resultado = clienteService.atualizar(1L, clienteAtualizado);

        assertNotNull(resultado);
        assertEquals("João da Silva Updated", resultado.getNome());
        assertEquals("joao.novo@email.com", resultado.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.atualizar(999L, clientePF));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoTentarAlterarDocumento() {
        Cliente clienteComDocumentoDiferente = new Cliente();
        clienteComDocumentoDiferente.setNome("João da Silva");
        clienteComDocumentoDiferente.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteComDocumentoDiferente.setDocumento("52998224725"); // documento diferente
        clienteComDocumentoDiferente.setEmail("joao.silva@email.com");
        clienteComDocumentoDiferente.setTelefone("11987654321");
        clienteComDocumentoDiferente.setEndereco(endereco);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));

        assertThrows(DocumentoNaoPodeSerAlteradoException.class, () -> clienteService.atualizar(1L, clienteComDocumentoDiferente));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComEmailJaCadastrado() {
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João da Silva");
        clienteAtualizado.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteAtualizado.setDocumento("11144477735");
        clienteAtualizado.setEmail("outro@email.com");
        clienteAtualizado.setTelefone("11987654321");
        clienteAtualizado.setEndereco(endereco);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));
        when(clienteRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> clienteService.atualizar(1L, clienteAtualizado));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoTentarAlterarTipoCliente() {
        Cliente clienteComTipoDiferente = new Cliente();
        clienteComTipoDiferente.setNome("João da Silva");
        clienteComTipoDiferente.setTipoCliente(TipoCliente.PESSOA_JURIDICA); // tipo diferente
        clienteComTipoDiferente.setDocumento("11144477735");
        clienteComTipoDiferente.setEmail("joao.silva@email.com");
        clienteComTipoDiferente.setTelefone("11987654321");
        clienteComTipoDiferente.setEndereco(endereco);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));

        assertThrows(IllegalArgumentException.class, () -> clienteService.atualizar(1L, clienteComTipoDiferente));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveDeletarClienteComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));
        doNothing().when(clienteRepository).delete(any(Cliente.class));

        assertDoesNotThrow(() -> clienteService.deletar(1L));

        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).delete(clientePF);
    }

    @Test
    void deveLancarExcecaoAoDeletarClienteInexistente() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.deletar(999L));

        verify(clienteRepository, never()).delete(any(Cliente.class));
    }

    @Test
    void deveVerificarSeExistePorDocumento() {
        when(clienteRepository.existsByDocumento("11144477735")).thenReturn(true);

        boolean resultado = clienteService.existePorDocumento("11144477735");

        assertTrue(resultado);
        verify(clienteRepository, times(1)).existsByDocumento("11144477735");
    }

    @Test
    void deveVerificarSeExistePorEmail() {
        when(clienteRepository.existsByEmail("joao.silva@email.com")).thenReturn(true);

        boolean resultado = clienteService.existePorEmail("joao.silva@email.com");

        assertTrue(resultado);
        verify(clienteRepository, times(1)).existsByEmail("joao.silva@email.com");
    }
}