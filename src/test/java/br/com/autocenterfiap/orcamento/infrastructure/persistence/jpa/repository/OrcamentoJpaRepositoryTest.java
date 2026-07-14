package br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.repository;

import br.com.autocenterfiap.cliente.domain.enums.TipoCliente;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.entity.ClienteJpaEntity;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.entity.EnderecoJpaEntity;
import br.com.autocenterfiap.cliente.infrastructure.persistence.jpa.repository.ClienteJpaRepository;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.entity.OrcamentoJpaEntity;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DisplayName("OrcamentoJpaRepository - Testes de Integração JPA")
class OrcamentoJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrcamentoJpaRepository orcamentoJpaRepository;

    @Autowired
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    @Autowired
    private VeiculoJpaRepository veiculoRepository;

    @Autowired
    private ClienteJpaRepository clienteRepository;

    private OrdemServicoJpaEntity ordemServicoJpaEntity;

    @BeforeEach
    void setUp() {
        orcamentoJpaRepository.deleteAll();
        orcamentoJpaRepository.flush();
        ordemServicoJpaRepository.deleteAll();
        ordemServicoJpaRepository.flush();
        veiculoRepository.deleteAll();
        veiculoRepository.flush();
        clienteRepository.deleteAll();
        clienteRepository.flush();
        entityManager.flush();

        EnderecoJpaEntity endereco = new EnderecoJpaEntity();
        endereco.setCep("01000-00");
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");

        ClienteJpaEntity cliente = new ClienteJpaEntity();
        cliente.setNome("João da Silva");
        cliente.setTipoCliente(TipoCliente.PESSOA_FISICA);
        cliente.setDocumento("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setEndereco(endereco);
        cliente = entityManager.persist(cliente);

        VeiculoJpaEntity veiculo = new VeiculoJpaEntity();
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
        veiculo = entityManager.persist(veiculo);

        ordemServicoJpaEntity = new OrdemServicoJpaEntity();
        ordemServicoJpaEntity.setNumeroOrdemServico(1001L);
        ordemServicoJpaEntity.setStatusOS(StatusOS.AGUARDANDO_APROVACAO);
        ordemServicoJpaEntity.setValorTotal(BigDecimal.valueOf(1000));
        ordemServicoJpaEntity.setCliente(cliente);
        ordemServicoJpaEntity.setVeiculo(veiculo);
        ordemServicoJpaEntity = entityManager.persist(ordemServicoJpaEntity);

        entityManager.flush();
    }

    @Test
    @DisplayName("Deve buscar orçamento aguardando aprovação por OS")
    void deveBuscarOrcamentoAguardandoAprovacaoPorOS() {
        OrcamentoJpaEntity orcamento = OrcamentoJpaEntity.builder()
                .ordemServicoJpaEntity(ordemServicoJpaEntity)
                .valorTotal(BigDecimal.valueOf(1000))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();
        entityManager.persist(orcamento);
        entityManager.flush();

        Optional<OrcamentoJpaEntity> resultado = orcamentoJpaRepository
                .buscarOrcamentoAguardandoAprovacaoPorOS(ordemServicoJpaEntity);

        assertTrue(resultado.isPresent());
        assertEquals(StatusOrcamento.AGUARDANDO_APROVACAO, resultado.get().getStatusOrcamento());
    }

    @Test
    @DisplayName("Deve retornar vazio quando não existir orçamento aguardando por OS")
    void deveRetornarVazioQuandoNaoExistirOrcamentoAguardandoPorOS() {
        Optional<OrcamentoJpaEntity> resultado = orcamentoJpaRepository
                .buscarOrcamentoAguardandoAprovacaoPorOS(ordemServicoJpaEntity);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve listar por status com paginação")
    void deveListarPorStatusComPaginacao() {
        entityManager.persist(OrcamentoJpaEntity.builder()
                .ordemServicoJpaEntity(ordemServicoJpaEntity)
                .valorTotal(BigDecimal.valueOf(1000))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build());
        entityManager.persist(OrcamentoJpaEntity.builder()
                .ordemServicoJpaEntity(ordemServicoJpaEntity)
                .valorTotal(BigDecimal.valueOf(800))
                .statusOrcamento(StatusOrcamento.APROVADO)
                .build());
        entityManager.flush();

        Page<OrcamentoJpaEntity> page = orcamentoJpaRepository.findByStatusOrcamento(
                StatusOrcamento.AGUARDANDO_APROVACAO, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals(StatusOrcamento.AGUARDANDO_APROVACAO, page.getContent().get(0).getStatusOrcamento());
    }

    @Test
    @DisplayName("Deve retornar página vazia para status sem orçamentos")
    void deveRetornarPaginaVaziaParaStatusSemOrcamentos() {
        entityManager.persist(OrcamentoJpaEntity.builder()
                .ordemServicoJpaEntity(ordemServicoJpaEntity)
                .valorTotal(BigDecimal.valueOf(1000))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build());
        entityManager.flush();

        Page<OrcamentoJpaEntity> page = orcamentoJpaRepository.findByStatusOrcamento(
                StatusOrcamento.APROVADO, PageRequest.of(0, 10));

        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
    }
}
