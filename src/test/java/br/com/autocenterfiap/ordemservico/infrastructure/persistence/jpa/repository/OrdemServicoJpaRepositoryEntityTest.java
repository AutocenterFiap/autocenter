package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.cliente.domain.enums.TipoCliente;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.entity.ClienteJpaEntity;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.entity.EnderecoJpaEntity;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.repository.ClienteJpaRepository;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.entity.VeiculoJpaEntity;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.repository.VeiculoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DisplayName("OrdemServicoRepository - Testes de Integração")
class OrdemServicoJpaRepositoryEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrdemServicoJpaRepository repository;

    @Autowired
    private VeiculoJpaRepository veiculoRepository;

    @Autowired
    private ClienteJpaRepository clienteRepository;

    private ClienteJpaEntity cliente;
    private VeiculoJpaEntity veiculo;
    private OrdemServicoJpaEntity ordemServicoJpaEntity;
    private OrdemServicoJpaEntity ordemServicoJpaEntitySecundaria;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.flush();
        veiculoRepository.deleteAll();
        veiculoRepository.flush();
        clienteRepository.deleteAll();
        clienteRepository.flush();
        entityManager.flush();

        cliente = new ClienteJpaEntity();
        cliente.setNome("João da Silva");
        cliente.setTipoCliente(TipoCliente.PESSOA_FISICA);
        cliente.setDocumento("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        EnderecoJpaEntity endereco = new EnderecoJpaEntity();
        endereco.setCep("01000-00");
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        cliente.setEndereco(endereco);

        veiculo = new VeiculoJpaEntity();
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

        ordemServicoJpaEntity = new OrdemServicoJpaEntity();
        ordemServicoJpaEntity.setNumeroOrdemServico(1001L);
        ordemServicoJpaEntity.setStatusOS(StatusOS.ABERTA);
        ordemServicoJpaEntity.setValorTotal(BigDecimal.ZERO);

        ordemServicoJpaEntitySecundaria = new OrdemServicoJpaEntity();
        ordemServicoJpaEntitySecundaria.setNumeroOrdemServico(1002L);
        ordemServicoJpaEntitySecundaria.setStatusOS(StatusOS.FINALIZADA);
        ordemServicoJpaEntitySecundaria.setValorTotal(BigDecimal.valueOf(150.0));
    }

    private void persistDependencies() {
        cliente = entityManager.persist(cliente);
        veiculo = entityManager.persist(veiculo);
        ordemServicoJpaEntity.setCliente(cliente);
        ordemServicoJpaEntity.setVeiculo(veiculo);
        ordemServicoJpaEntitySecundaria.setCliente(cliente);
        ordemServicoJpaEntitySecundaria.setVeiculo(veiculo);
    }

    @Test
    public void deveSalvarOrdemServicoQuandoValida() {
        persistDependencies();
        OrdemServicoJpaEntity salvo = entityManager.persist(ordemServicoJpaEntity);
        entityManager.flush();

        assertNotNull(salvo.getId());
        assertEquals(1001L, salvo.getNumeroOrdemServico());
        assertEquals(StatusOS.ABERTA, salvo.getStatusOS());
    }

    @Test
    public void deveRetornarListaDeOrdensDeServicoQuandoHouverCadastradas() {
        persistDependencies();
        entityManager.persist(ordemServicoJpaEntity);
        entityManager.persist(ordemServicoJpaEntitySecundaria);
        entityManager.flush();

        List<OrdemServicoJpaEntity> ordens = repository.findAll();

        assertNotNull(ordens);
        assertEquals(2, ordens.size());
    }

    @Test
    public void deveDeletarOrdemServicoQuandoIdExistir() {
        persistDependencies();
        OrdemServicoJpaEntity salvo = entityManager.persist(ordemServicoJpaEntity);
        entityManager.flush();

        repository.deleteById(salvo.getId());
        entityManager.flush();

        Optional<OrdemServicoJpaEntity> deletado = repository.findById(salvo.getId());
        assertTrue(deletado.isEmpty());
    }

    @Test
    public void deveRetornarListaDeOrdensQuandoBuscarPorStatusExistente() {
        persistDependencies();
        entityManager.persist(ordemServicoJpaEntity);
        entityManager.persist(ordemServicoJpaEntitySecundaria);
        entityManager.flush();

        List<OrdemServicoJpaEntity> encontradas = repository.findByStatus(StatusOS.ABERTA, Pageable.unpaged()).getContent();

        assertNotNull(encontradas);
        assertEquals(1, encontradas.size());
        assertEquals(StatusOS.ABERTA, encontradas.get(0).getStatusOS());
    }

    @Test
    public void deveRetornarListaVaziaQuandoBuscarPorStatusInexistente() {
        persistDependencies();
        entityManager.persist(ordemServicoJpaEntity);
        entityManager.flush();

        List<OrdemServicoJpaEntity> encontradas = repository.findByStatus(StatusOS.CANCELADA, Pageable.unpaged()).getContent();

        assertNotNull(encontradas);
        assertTrue(encontradas.isEmpty());
    }

    @Test
    public void deveRetornarOrdemServicoQuandoBuscarPorNumeroExistente() {
        persistDependencies();
        entityManager.persist(ordemServicoJpaEntity);
        entityManager.flush();

        Optional<OrdemServicoJpaEntity> encontrada = repository.findByNumeroOrdemServico(1001L);

        assertTrue(encontrada.isPresent());
        assertEquals(1001L, encontrada.get().getNumeroOrdemServico());
    }

    @Test
    public void deveRetornarVazioQuandoBuscarPorNumeroInexistente() {
        Optional<OrdemServicoJpaEntity> encontrada = repository.findByNumeroOrdemServico(9999L);
        assertTrue(encontrada.isEmpty());
    }


    private OrdemServicoJpaEntity novaOrdem(Long numero, StatusOS status) {
        OrdemServicoJpaEntity os = new OrdemServicoJpaEntity();
        os.setNumeroOrdemServico(numero);
        os.setStatusOS(status);
        os.setValorTotal(BigDecimal.ZERO);
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        return os;
    }

    @Test
    public void deveExcluirDaListagemAsOrdensFinalizadasEEntregues() {
        persistDependencies();
        entityManager.persist(novaOrdem(2001L, StatusOS.RECEBIDA));
        entityManager.persist(novaOrdem(2002L, StatusOS.FINALIZADA));
        entityManager.persist(novaOrdem(2003L, StatusOS.ENTREGUE));
        entityManager.flush();

        List<OrdemServicoJpaEntity> ativas =
                repository.findAtivasOrdenadasPorPrioridade(Pageable.unpaged()).getContent();

        assertEquals(1, ativas.size());
        assertEquals(StatusOS.RECEBIDA, ativas.get(0).getStatusOS());
    }

    @Test
    public void deveOrdenarPorPrioridadeDeStatus() {
        persistDependencies();
        entityManager.persist(novaOrdem(3001L, StatusOS.RECEBIDA));
        entityManager.persist(novaOrdem(3002L, StatusOS.EM_EXECUCAO));
        entityManager.persist(novaOrdem(3003L, StatusOS.EM_DIAGNOSTICO));
        entityManager.persist(novaOrdem(3004L, StatusOS.AGUARDANDO_APROVACAO));
        entityManager.flush();

        List<OrdemServicoJpaEntity> ativas =
                repository.findAtivasOrdenadasPorPrioridade(Pageable.unpaged()).getContent();

        assertEquals(4, ativas.size());
        assertEquals(StatusOS.EM_EXECUCAO, ativas.get(0).getStatusOS());
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, ativas.get(1).getStatusOS());
        assertEquals(StatusOS.EM_DIAGNOSTICO, ativas.get(2).getStatusOS());
        assertEquals(StatusOS.RECEBIDA, ativas.get(3).getStatusOS());
    }

    @Test
    public void deveOrdenarMaisAntigasPrimeiroDentroDoMesmoStatus() {
        persistDependencies();
        OrdemServicoJpaEntity maisAntiga = entityManager.persist(novaOrdem(4001L, StatusOS.RECEBIDA));
        OrdemServicoJpaEntity maisNova = entityManager.persist(novaOrdem(4002L, StatusOS.RECEBIDA));
        entityManager.flush();

        List<OrdemServicoJpaEntity> ativas =
                repository.findAtivasOrdenadasPorPrioridade(Pageable.unpaged()).getContent();

        assertEquals(2, ativas.size());
        assertEquals(maisAntiga.getId(), ativas.get(0).getId());
        assertEquals(maisNova.getId(), ativas.get(1).getId());
    }

    @Test
    public void deveRetornarTrueQuandoVeiculoTiverOrdemServicoComStatusEspecifico() {
        persistDependencies();
        entityManager.persist(ordemServicoJpaEntity); // Status ABERTA
        entityManager.flush();

        boolean existe = repository.existsByVeiculoIdAndStatusOSIn(veiculo.getId(), List.of(StatusOS.ABERTA, StatusOS.RECEBIDA));

        assertTrue(existe);
    }

    @Test
    public void deveRetornarFalseQuandoVeiculoTiverOrdemServicoMasComOutroStatus() {
        persistDependencies();
        entityManager.persist(ordemServicoJpaEntity); // Status ABERTA
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
