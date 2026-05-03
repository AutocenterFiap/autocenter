package br.com.autocenterfiap.cliente.repository;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.model.Endereco;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ClienteRepository - Testes de Integração")
class ClienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    private Cliente clientePF;
    private Cliente clientePJ;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        ordemServicoRepository.deleteAll();
        ordemServicoRepository.flush();
        clienteRepository.deleteAll();
        clienteRepository.flush();
        entityManager.flush();

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
        clientePF.setNome("João da Silva");
        clientePF.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clientePF.setDocumento("11144477735");
        clientePF.setEmail("joao.silva@email.com");
        clientePF.setTelefone("11987654321");
        clientePF.setEndereco(endereco);
        clientePF.setDataNascimento(LocalDate.of(1990, 5, 15));

        // Setup Cliente Pessoa Jurídica
        clientePJ = new Cliente();
        clientePJ.setNome("Empresa ABC LTDA");
        clientePJ.setTipoCliente(TipoCliente.PESSOA_JURIDICA);
        clientePJ.setDocumento("11222333000181");
        clientePJ.setEmail("contato@empresaabc.com");
        clientePJ.setTelefone("1133334444");
        clientePJ.setEndereco(endereco);
    }

    @Test
    void deveSalvarClientePessoaFisicaComSucesso() {
        Cliente clienteSalvo = clienteRepository.save(clientePF);

        assertNotNull(clienteSalvo.getId());
        assertEquals("João da Silva", clienteSalvo.getNome());
        assertEquals(TipoCliente.PESSOA_FISICA, clienteSalvo.getTipoCliente());
        assertEquals("11144477735", clienteSalvo.getDocumento());
        assertEquals("joao.silva@email.com", clienteSalvo.getEmail());
        assertEquals("01310100", clienteSalvo.getEndereco().getCep());
    }

    @Test
    void deveSalvarClientePessoaJuridicaComSucesso() {
        Cliente clienteSalvo = clienteRepository.save(clientePJ);

        assertNotNull(clienteSalvo.getId());
        assertEquals("Empresa ABC LTDA", clienteSalvo.getNome());
        assertEquals(TipoCliente.PESSOA_JURIDICA, clienteSalvo.getTipoCliente());
        assertEquals("11222333000181", clienteSalvo.getDocumento());
    }

    @Test
    void deveBuscarClientePorDocumentoComSucesso() {
        entityManager.persist(clientePF);
        entityManager.flush();

        Optional<Cliente> clienteEncontrado = clienteRepository.findByDocumento("11144477735");

        assertTrue(clienteEncontrado.isPresent());
        assertEquals("João da Silva", clienteEncontrado.get().getNome());
        assertEquals("11144477735", clienteEncontrado.get().getDocumento());
    }

    @Test
    void deveRetornarOptionalVazioAoBuscarPorDocumentoInexistente() {
        Optional<Cliente> clienteEncontrado = clienteRepository.findByDocumento("99999999999");

        assertFalse(clienteEncontrado.isPresent());
    }

    @Test
    void deveBuscarClientePorEmailComSucesso() {
        entityManager.persist(clientePF);
        entityManager.flush();

        Optional<Cliente> clienteEncontrado = clienteRepository.findByEmail("joao.silva@email.com");

        assertTrue(clienteEncontrado.isPresent());
        assertEquals("João da Silva", clienteEncontrado.get().getNome());
        assertEquals("joao.silva@email.com", clienteEncontrado.get().getEmail());
    }

    @Test
    void deveRetornarOptionalVazioAoBuscarPorEmailInexistente() {
        Optional<Cliente> clienteEncontrado = clienteRepository.findByEmail("naoexiste@email.com");

        assertFalse(clienteEncontrado.isPresent());
    }

    @Test
    void deveVerificarSeExisteClientePorDocumento() {
        entityManager.persist(clientePF);
        entityManager.flush();

        boolean existe = clienteRepository.existsByDocumento("11144477735");
        boolean naoExiste = clienteRepository.existsByDocumento("99999999999");

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    void deveVerificarSeExisteClientePorEmail() {
        entityManager.persist(clientePF);
        entityManager.flush();

        boolean existe = clienteRepository.existsByEmail("joao.silva@email.com");
        boolean naoExiste = clienteRepository.existsByEmail("naoexiste@email.com");

        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        Cliente clienteSalvo = entityManager.persist(clientePF);
        entityManager.flush();

        clienteSalvo.setNome("João da Silva Updated");
        clienteSalvo.setTelefone("11999998888");
        Cliente clienteAtualizado = clienteRepository.save(clienteSalvo);
        entityManager.flush();

        assertEquals("João da Silva Updated", clienteAtualizado.getNome());
        assertEquals("11999998888", clienteAtualizado.getTelefone());
        assertEquals(clienteSalvo.getId(), clienteAtualizado.getId());
    }

    @Test
    void deveDeletarClienteComSucesso() {
        Cliente clienteSalvo = entityManager.persist(clientePF);
        entityManager.flush();
        Long clienteId = clienteSalvo.getId();

        clienteRepository.delete(clienteSalvo);
        entityManager.flush();

        Optional<Cliente> clienteDeletado = clienteRepository.findById(clienteId);
        assertFalse(clienteDeletado.isPresent());
    }

    @Test
    void deveBuscarTodosOsClientes() {
        entityManager.persist(clientePF);
        entityManager.persist(clientePJ);
        entityManager.flush();

        var clientes = clienteRepository.findAll();

        assertNotNull(clientes);
        assertEquals(2, clientes.size());
    }

    @Test
    void deveGarantirUnicidadeDoDocumento() {
        entityManager.persist(clientePF);
        entityManager.flush();

        Cliente clienteDuplicado = new Cliente();
        clienteDuplicado.setNome("Maria da Silva");
        clienteDuplicado.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteDuplicado.setDocumento("11144477735"); // mesmo documento
        clienteDuplicado.setEmail("maria@email.com");
        clienteDuplicado.setTelefone("11888887777");
        clienteDuplicado.setEndereco(endereco);

        assertThrows(Exception.class, () -> {
            entityManager.persist(clienteDuplicado);
            entityManager.flush();
        });
    }

    @Test
    void deveGarantirUnicidadeDoEmail() {
        entityManager.persist(clientePF);
        entityManager.flush();

        Cliente clienteDuplicado = new Cliente();
        clienteDuplicado.setNome("Maria da Silva");
        clienteDuplicado.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteDuplicado.setDocumento("52998224725");
        clienteDuplicado.setEmail("joao.silva@email.com"); // mesmo email
        clienteDuplicado.setTelefone("11888887777");
        clienteDuplicado.setEndereco(endereco);

        assertThrows(Exception.class, () -> {
            entityManager.persist(clienteDuplicado);
            entityManager.flush();
        });
    }
}
