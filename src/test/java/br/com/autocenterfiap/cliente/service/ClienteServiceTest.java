package br.com.autocenterfiap.cliente.service;

import br.com.autocenterfiap.cliente.dto.ClienteDTO;
import br.com.autocenterfiap.cliente.dto.ClienteResponseDTO;
import br.com.autocenterfiap.cliente.dto.EnderecoDTO;
import br.com.autocenterfiap.cliente.enums.TipoCliente;
import br.com.autocenterfiap.cliente.exception.ClienteNaoEncontradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoInvalidoException;
import br.com.autocenterfiap.cliente.exception.DocumentoJaCadastradoException;
import br.com.autocenterfiap.cliente.exception.DocumentoNaoPodeSerAlteradoException;
import br.com.autocenterfiap.cliente.exception.EmailJaCadastradoException;
import br.com.autocenterfiap.cliente.mapper.ClienteMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Testes Unitários")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clientePF;
    private Cliente clientePJ;
    private Endereco endereco;
    private ClienteDTO clienteDtoPF;
    private ClienteDTO clienteDtoPJ;
    private EnderecoDTO enderecoDTO;
    private ClienteResponseDTO clienteResponseDtoPF;
    private ClienteResponseDTO clienteResponseDtoPJ;

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

        // Setup EnderecoDTO
        enderecoDTO = EnderecoDTO.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .numero("1578")
                .complemento("Andar 5")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .estado("SP")
                .build();

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

        // Setup ClienteDTO Pessoa Física
        clienteDtoPF = ClienteDTO.builder()
                .nome("João da Silva")
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .documento("11144477735")
                .email("joao.silva@email.com")
                .telefone("11987654321")
                .endereco(enderecoDTO)
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .build();

        // Setup ClienteResponseDTO Pessoa Física
        clienteResponseDtoPF = ClienteResponseDTO.builder()
                .id(1L)
                .nome("João da Silva")
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .documento("11144477735")
                .email("joao.silva@email.com")
                .telefone("11987654321")
                .endereco(enderecoDTO)
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .build();

        // Setup Cliente Pessoa Jurídica
        clientePJ = new Cliente();
        clientePJ.setId(2L);
        clientePJ.setNome("Empresa ABC LTDA");
        clientePJ.setTipoCliente(TipoCliente.PESSOA_JURIDICA);
        clientePJ.setDocumento("11222333000181");
        clientePJ.setEmail("contato@empresaabc.com");
        clientePJ.setTelefone("1133334444");
        clientePJ.setEndereco(endereco);

        // Setup ClienteDTO Pessoa Jurídica
        clienteDtoPJ = ClienteDTO.builder()
                .nome("Empresa ABC LTDA")
                .tipoCliente(TipoCliente.PESSOA_JURIDICA)
                .documento("11222333000181")
                .email("contato@empresaabc.com")
                .telefone("1133334444")
                .endereco(enderecoDTO)
                .build();

        // Setup ClienteResponseDTO Pessoa Jurídica
        clienteResponseDtoPJ = ClienteResponseDTO.builder()
                .id(2L)
                .nome("Empresa ABC LTDA")
                .tipoCliente(TipoCliente.PESSOA_JURIDICA)
                .documento("11222333000181")
                .email("contato@empresaabc.com")
                .telefone("1133334444")
                .endereco(enderecoDTO)
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os clientes com paginação")
    void deveListarTodosOsClientes() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Cliente> clientesEsperados = Arrays.asList(clientePF, clientePJ);
        Page<Cliente> clientesPage = new PageImpl<>(clientesEsperados, pageable, clientesEsperados.size());

        when(clienteRepository.findAll(pageable)).thenReturn(clientesPage);
        when(clienteMapper.toResponseDTO(clientePF)).thenReturn(clienteResponseDtoPF);
        when(clienteMapper.toResponseDTO(clientePJ)).thenReturn(clienteResponseDtoPJ);

        Page<ClienteResponseDTO> resultado = clienteService.listarTodos(pageable);

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals(2, resultado.getContent().size());
        assertEquals("João da Silva", resultado.getContent().get(0).getNome());
        assertEquals("Empresa ABC LTDA", resultado.getContent().get(1).getNome());
        verify(clienteRepository, times(1)).findAll(pageable);
        verify(clienteMapper, times(2)).toResponseDTO(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver clientes")
    void deveRetornarListaVaziaQuandoNaoHouverClientes() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Cliente> clientesPage = new PageImpl<>(List.of(), pageable, 0);

        when(clienteRepository.findAll(pageable)).thenReturn(clientesPage);

        Page<ClienteResponseDTO> resultado = clienteService.listarTodos(pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());
        verify(clienteRepository, times(1)).findAll(pageable);
    }

    @Test
    void deveBuscarClientePorIdComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));
        when(clienteMapper.toResponseDTO(clientePF)).thenReturn(clienteResponseDtoPF);

        ResponseEntity<ClienteResponseDTO> resultado = clienteService.buscarPorId(1L);

        assertTrue(resultado.getStatusCode().is2xxSuccessful());
        assertNotNull(resultado.getBody());
        assertEquals("João da Silva", resultado.getBody().getNome());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteMapper, times(1)).toResponseDTO(clientePF);
    }

    @Test
    void deveRetornarOptionalVazioQuandoClienteNaoExiste() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<ClienteResponseDTO> resultado = clienteService.buscarPorId(999L);

        assertTrue(resultado.getStatusCode().is4xxClientError());
        verify(clienteRepository, times(1)).findById(999L);
    }

    @Test
    void deveBuscarClientePorDocumentoComSucesso() {
        when(clienteRepository.findByDocumento("11144477735")).thenReturn(Optional.of(clientePF));
        when(clienteMapper.toResponseDTO(clientePF)).thenReturn(clienteResponseDtoPF);

        ResponseEntity<ClienteResponseDTO> resultado = clienteService.buscarPorDocumento("11144477735");

        assertTrue(resultado.getStatusCode().is2xxSuccessful());
        assertNotNull(resultado.getBody());
        assertEquals("João da Silva", resultado.getBody().getNome());
        verify(clienteRepository, times(1)).findByDocumento("11144477735");
        verify(clienteMapper, times(1)).toResponseDTO(clientePF);
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
        Cliente clienteSemId = new Cliente();
        clienteSemId.setNome(clientePF.getNome());
        clienteSemId.setTipoCliente(clientePF.getTipoCliente());
        clienteSemId.setDocumento(clientePF.getDocumento());
        clienteSemId.setEmail(clientePF.getEmail());
        clienteSemId.setTelefone(clientePF.getTelefone());
        clienteSemId.setEndereco(clientePF.getEndereco());
        clienteSemId.setDataNascimento(clientePF.getDataNascimento());

        when(clienteMapper.toEntity(clienteDtoPF)).thenReturn(clienteSemId);
        when(clienteRepository.existsByDocumento(anyString())).thenReturn(false);
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clientePF);
        when(clienteMapper.toResponseDTO(clientePF)).thenReturn(clienteResponseDtoPF);

        ClienteResponseDTO resultado = clienteService.criar(clienteDtoPF);

        assertNotNull(resultado);
        assertEquals("João da Silva", resultado.getNome());
        assertEquals(TipoCliente.PESSOA_FISICA, resultado.getTipoCliente());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(clienteMapper, times(1)).toEntity(clienteDtoPF);
        verify(clienteMapper, times(1)).toResponseDTO(clientePF);
    }

    @Test
    void deveCriarClientePessoaJuridicaComSucesso() {
        Cliente clienteSemId = new Cliente();
        clienteSemId.setNome(clientePJ.getNome());
        clienteSemId.setTipoCliente(clientePJ.getTipoCliente());
        clienteSemId.setDocumento(clientePJ.getDocumento());
        clienteSemId.setEmail(clientePJ.getEmail());
        clienteSemId.setTelefone(clientePJ.getTelefone());
        clienteSemId.setEndereco(clientePJ.getEndereco());

        when(clienteMapper.toEntity(clienteDtoPJ)).thenReturn(clienteSemId);
        when(clienteRepository.existsByDocumento(anyString())).thenReturn(false);
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clientePJ);
        when(clienteMapper.toResponseDTO(clientePJ)).thenReturn(clienteResponseDtoPJ);

        ClienteResponseDTO resultado = clienteService.criar(clienteDtoPJ);

        assertNotNull(resultado);
        assertEquals("Empresa ABC LTDA", resultado.getNome());
        assertEquals(TipoCliente.PESSOA_JURIDICA, resultado.getTipoCliente());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(clienteMapper, times(1)).toEntity(clienteDtoPJ);
        verify(clienteMapper, times(1)).toResponseDTO(clientePJ);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com documento já cadastrado")
    void deveLancarExcecaoAoCriarClienteComDocumentoJaCadastrado() {
        when(clienteMapper.toEntity(clienteDtoPF)).thenReturn(clientePF);
        when(clienteRepository.existsByDocumento("11144477735")).thenReturn(true);

        assertThrows(DocumentoJaCadastradoException.class, () -> clienteService.criar(clienteDtoPF));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClienteComEmailJaCadastrado() {
        when(clienteMapper.toEntity(clienteDtoPF)).thenReturn(clientePF);
        when(clienteRepository.existsByDocumento(anyString())).thenReturn(false);
        when(clienteRepository.existsByEmail("joao.silva@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> clienteService.criar(clienteDtoPF));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClientePFComCpfInvalido() {
        ClienteDTO clienteInvalido = ClienteDTO.builder()
                .nome("João da Silva")
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .documento("12345678901") // CPF inválido
                .email("joao.silva@email.com")
                .telefone("11987654321")
                .endereco(enderecoDTO)
                .build();

        Cliente clienteEntity = new Cliente();
        clienteEntity.setDocumento("12345678901");
        clienteEntity.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteEntity.setNome("João da Silva");
        clienteEntity.setEmail("joao.silva@email.com");
        clienteEntity.setTelefone("11987654321");
        clienteEntity.setEndereco(endereco);

        when(clienteMapper.toEntity(clienteInvalido)).thenReturn(clienteEntity);

        assertThrows(DocumentoInvalidoException.class, () -> clienteService.criar(clienteInvalido));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClientePJComCnpjInvalido() {
        ClienteDTO clienteInvalido = ClienteDTO.builder()
                .nome("Empresa ABC LTDA")
                .tipoCliente(TipoCliente.PESSOA_JURIDICA)
                .documento("12345678901234") // CNPJ inválido
                .email("contato@empresaabc.com")
                .telefone("1133334444")
                .endereco(enderecoDTO)
                .build();

        Cliente clienteEntity = new Cliente();
        clienteEntity.setDocumento("12345678901234");
        clienteEntity.setTipoCliente(TipoCliente.PESSOA_JURIDICA);
        clienteEntity.setNome("Empresa ABC LTDA");
        clienteEntity.setEmail("contato@empresaabc.com");
        clienteEntity.setTelefone("1133334444");
        clienteEntity.setEndereco(endereco);

        when(clienteMapper.toEntity(clienteInvalido)).thenReturn(clienteEntity);

        assertThrows(DocumentoInvalidoException.class, () -> clienteService.criar(clienteInvalido));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClientePFComCnpj() {
        ClienteDTO clienteInvalido = ClienteDTO.builder()
                .nome("João da Silva")
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .documento("11222333000181") // CNPJ com tipo PF
                .email("joao.silva@email.com")
                .telefone("11987654321")
                .endereco(enderecoDTO)
                .build();

        Cliente clienteEntity = new Cliente();
        clienteEntity.setDocumento("11222333000181");
        clienteEntity.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteEntity.setNome("João da Silva");
        clienteEntity.setEmail("joao.silva@email.com");
        clienteEntity.setTelefone("11987654321");
        clienteEntity.setEndereco(endereco);

        when(clienteMapper.toEntity(clienteInvalido)).thenReturn(clienteEntity);

        assertThrows(IllegalArgumentException.class, () -> clienteService.criar(clienteInvalido));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoCriarClientePJComCpf() {
        ClienteDTO clienteInvalido = ClienteDTO.builder()
                .nome("Empresa ABC LTDA")
                .tipoCliente(TipoCliente.PESSOA_JURIDICA)
                .documento("11144477735") // CPF com tipo PJ
                .email("contato@empresaabc.com")
                .telefone("1133334444")
                .endereco(enderecoDTO)
                .build();

        Cliente clienteEntity = new Cliente();
        clienteEntity.setDocumento("11144477735");
        clienteEntity.setTipoCliente(TipoCliente.PESSOA_JURIDICA);
        clienteEntity.setNome("Empresa ABC LTDA");
        clienteEntity.setEmail("contato@empresaabc.com");
        clienteEntity.setTelefone("1133334444");
        clienteEntity.setEndereco(endereco);

        when(clienteMapper.toEntity(clienteInvalido)).thenReturn(clienteEntity);

        assertThrows(IllegalArgumentException.class, () -> clienteService.criar(clienteInvalido));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        ClienteDTO clienteAtualizadoDTO = ClienteDTO.builder()
                .nome("João da Silva Updated")
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .documento("11144477735") // mesmo documento
                .email("joao.novo@email.com")
                .telefone("11999998888")
                .endereco(enderecoDTO)
                .build();

        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João da Silva Updated");
        clienteAtualizado.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteAtualizado.setDocumento("11144477735");
        clienteAtualizado.setEmail("joao.novo@email.com");
        clienteAtualizado.setTelefone("11999998888");
        clienteAtualizado.setEndereco(endereco);

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome("João da Silva Updated");
        clienteSalvo.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteSalvo.setDocumento("11144477735");
        clienteSalvo.setEmail("joao.novo@email.com");
        clienteSalvo.setTelefone("11999998888");
        clienteSalvo.setEndereco(endereco);

        ClienteResponseDTO responseAtualizado = ClienteResponseDTO.builder()
                .id(1L)
                .nome("João da Silva Updated")
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .documento("11144477735")
                .email("joao.novo@email.com")
                .telefone("11999998888")
                .endereco(enderecoDTO)
                .build();

        when(clienteMapper.toEntity(clienteAtualizadoDTO)).thenReturn(clienteAtualizado);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));
        when(clienteRepository.existsByEmail("joao.novo@email.com")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(clienteMapper.toResponseDTO(clienteSalvo)).thenReturn(responseAtualizado);

        ClienteResponseDTO resultado = clienteService.atualizar(1L, clienteAtualizadoDTO);

        assertNotNull(resultado);
        assertEquals("João da Silva Updated", resultado.getNome());
        assertEquals("joao.novo@email.com", resultado.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(clienteMapper, times(1)).toEntity(clienteAtualizadoDTO);
        verify(clienteMapper, times(1)).toResponseDTO(clienteSalvo);
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        when(clienteMapper.toEntity(clienteDtoPF)).thenReturn(clientePF);
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.atualizar(999L, clienteDtoPF));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoTentarAlterarDocumento() {
        ClienteDTO clienteComDocumentoDiferenteDTO = ClienteDTO.builder()
                .nome("João da Silva")
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .documento("52998224725") // documento diferente
                .email("joao.silva@email.com")
                .telefone("11987654321")
                .endereco(enderecoDTO)
                .build();

        Cliente clienteComDocumentoDiferente = new Cliente();
        clienteComDocumentoDiferente.setNome("João da Silva");
        clienteComDocumentoDiferente.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteComDocumentoDiferente.setDocumento("52998224725");
        clienteComDocumentoDiferente.setEmail("joao.silva@email.com");
        clienteComDocumentoDiferente.setTelefone("11987654321");
        clienteComDocumentoDiferente.setEndereco(endereco);

        when(clienteMapper.toEntity(clienteComDocumentoDiferenteDTO)).thenReturn(clienteComDocumentoDiferente);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));

        assertThrows(DocumentoNaoPodeSerAlteradoException.class,
            () -> clienteService.atualizar(1L, clienteComDocumentoDiferenteDTO));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComEmailJaCadastrado() {
        ClienteDTO clienteAtualizadoDTO = ClienteDTO.builder()
                .nome("João da Silva")
                .tipoCliente(TipoCliente.PESSOA_FISICA)
                .documento("11144477735")
                .email("outro@email.com")
                .telefone("11987654321")
                .endereco(enderecoDTO)
                .build();

        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João da Silva");
        clienteAtualizado.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteAtualizado.setDocumento("11144477735");
        clienteAtualizado.setEmail("outro@email.com");
        clienteAtualizado.setTelefone("11987654321");
        clienteAtualizado.setEndereco(endereco);

        when(clienteMapper.toEntity(clienteAtualizadoDTO)).thenReturn(clienteAtualizado);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));
        when(clienteRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> clienteService.atualizar(1L, clienteAtualizadoDTO));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoTentarAlterarTipoCliente() {
        ClienteDTO clienteComTipoDiferenteDTO = ClienteDTO.builder()
                .nome("João da Silva")
                .tipoCliente(TipoCliente.PESSOA_JURIDICA) // tipo diferente
                .documento("11144477735")
                .email("joao.silva@email.com")
                .telefone("11987654321")
                .endereco(enderecoDTO)
                .build();

        Cliente clienteComTipoDiferente = new Cliente();
        clienteComTipoDiferente.setNome("João da Silva");
        clienteComTipoDiferente.setTipoCliente(TipoCliente.PESSOA_JURIDICA);
        clienteComTipoDiferente.setDocumento("11144477735");
        clienteComTipoDiferente.setEmail("joao.silva@email.com");
        clienteComTipoDiferente.setTelefone("11987654321");
        clienteComTipoDiferente.setEndereco(endereco);

        when(clienteMapper.toEntity(clienteComTipoDiferenteDTO)).thenReturn(clienteComTipoDiferente);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePF));

        assertThrows(IllegalArgumentException.class, () -> clienteService.atualizar(1L, clienteComTipoDiferenteDTO));

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