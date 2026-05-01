package br.com.autocenterfiap.ordemservico.repository;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.model.Endereco;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.veiculo.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.model.Veiculo;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DisplayName("OrdemServicoRepository - Testes de Integração")
class OrdemServicoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrdemServicoRepository repository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente;
    private Veiculo veiculo;
    private OrdemServico ordemServico;
    private OrdemServico ordemServicoSecundaria;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.flush();
        veiculoRepository.deleteAll();
        veiculoRepository.flush();
        clienteRepository.deleteAll();
        clienteRepository.flush();
        entityManager.flush();

        cliente = new Cliente();
        cliente.setNome("João da Silva");
        cliente.setTipoCliente(TipoCliente.PESSOA_FISICA);
        cliente.setDocumento("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        Endereco endereco = new Endereco();
        endereco.setCep("01000-00");
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        cliente.setEndereco(endereco);

        veiculo = new Veiculo();
        veiculo.setPlaca("ABC1D23");
        veiculo.setChassi("9BWZZZ377VT004251");
        veiculo.setRenavam("82106426707");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAnoFabricacao(2020);
        veiculo.setAnoModelo(2021);
        veiculo.setCor("Preto");
        veiculo.setQuilometragem(45000L);
        veiculo.setTipoCombustivel(TipoCombustivel.DIESEL);
        veiculo.setCategoriaVeiculo(CategoriaVeiculo.CARRO);

        ordemServico = new OrdemServico();
        ordemServico.setNumeroOrdemServico(1001L);
        ordemServico.setStatusOS(StatusOS.ABERTA);
        ordemServico.setValorTotal(BigDecimal.ZERO);

        ordemServicoSecundaria = new OrdemServico();
        ordemServicoSecundaria.setNumeroOrdemServico(1002L);
        ordemServicoSecundaria.setStatusOS(StatusOS.FINALIZADA);
        ordemServicoSecundaria.setValorTotal(BigDecimal.valueOf(150.0));
    }

    private void persistDependencies() {
        cliente = entityManager.persist(cliente);
        veiculo = entityManager.persist(veiculo);
        ordemServico.setCliente(cliente);
        ordemServico.setVeiculo(veiculo);
        ordemServicoSecundaria.setCliente(cliente);
        ordemServicoSecundaria.setVeiculo(veiculo);
    }

    @Test
    public void deveSalvarOrdemServicoQuandoValida() {
        persistDependencies();
        OrdemServico salvo = entityManager.persist(ordemServico);
        entityManager.flush();

        assertNotNull(salvo.getId());
        assertEquals(1001L, salvo.getNumeroOrdemServico());
        assertEquals(StatusOS.ABERTA, salvo.getStatusOS());
    }

    @Test
    public void deveRetornarListaDeOrdensDeServicoQuandoHouverCadastradas() {
        persistDependencies();
        entityManager.persist(ordemServico);
        entityManager.persist(ordemServicoSecundaria);
        entityManager.flush();

        List<OrdemServico> ordens = repository.findAll();

        assertNotNull(ordens);
        assertEquals(2, ordens.size());
    }

    @Test
    public void deveDeletarOrdemServicoQuandoIdExistir() {
        persistDependencies();
        OrdemServico salvo = entityManager.persist(ordemServico);
        entityManager.flush();

        repository.deleteById(salvo.getId());
        entityManager.flush();

        Optional<OrdemServico> deletado = repository.findById(salvo.getId());
        assertTrue(deletado.isEmpty());
    }

    @Test
    public void deveRetornarListaDeOrdensQuandoBuscarPorStatusExistente() {
        persistDependencies();
        entityManager.persist(ordemServico);
        entityManager.persist(ordemServicoSecundaria);
        entityManager.flush();

        List<OrdemServico> encontradas = repository.findByStatus(StatusOS.ABERTA);

        assertNotNull(encontradas);
        assertEquals(1, encontradas.size());
        assertEquals(StatusOS.ABERTA, encontradas.get(0).getStatusOS());
    }

    @Test
    public void deveRetornarListaVaziaQuandoBuscarPorStatusInexistente() {
        persistDependencies();
        entityManager.persist(ordemServico);
        entityManager.flush();

        List<OrdemServico> encontradas = repository.findByStatus(StatusOS.CANCELADA);

        assertNotNull(encontradas);
        assertTrue(encontradas.isEmpty());
    }

    @Test
    public void deveRetornarOrdemServicoQuandoBuscarPorNumeroExistente() {
        persistDependencies();
        entityManager.persist(ordemServico);
        entityManager.flush();

        Optional<OrdemServico> encontrada = repository.findByNumeroOrdemServico(1001L);

        assertTrue(encontrada.isPresent());
        assertEquals(1001L, encontrada.get().getNumeroOrdemServico());
    }

    @Test
    public void deveRetornarVazioQuandoBuscarPorNumeroInexistente() {
        Optional<OrdemServico> encontrada = repository.findByNumeroOrdemServico(9999L);
        assertTrue(encontrada.isEmpty());
    }


    @Test
    public void deveRetornarTrueQuandoVeiculoTiverOrdemServicoComStatusEspecifico() {
        persistDependencies();
        entityManager.persist(ordemServico); // Status ABERTA
        entityManager.flush();

        boolean existe = repository.existsByVeiculoIdAndStatusOSIn(veiculo.getId(), List.of(StatusOS.ABERTA, StatusOS.RECEBIDA));

        assertTrue(existe);
    }

    @Test
    public void deveRetornarFalseQuandoVeiculoTiverOrdemServicoMasComOutroStatus() {
        persistDependencies();
        entityManager.persist(ordemServico); // Status ABERTA
        entityManager.flush();

        boolean existe = repository.existsByVeiculoIdAndStatusOSIn(veiculo.getId(), List.of(StatusOS.FINALIZADA, StatusOS.CANCELADA));

        assertFalse(existe);
    }

    @Test
    public void deveRetornarFalseQuandoVeiculoNaoTiverOrdemServico() {
        persistDependencies();
        entityManager.flush();

        boolean existe = repository.existsByVeiculoIdAndStatusOSIn(veiculo.getId(), List.of(StatusOS.ABERTA, StatusOS.RECEBIDA));

        assertFalse(existe);
    }
}
