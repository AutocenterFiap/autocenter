package br.com.autocenterfiap.orcamento.adapter.in;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.model.Endereco;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.orcamento.domain.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.domain.enums.TipoEnvio;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.entity.OrcamentoJpaEntity;
import br.com.autocenterfiap.orcamento.infrastructure.persistence.jpa.repository.OrcamentoJpaRepository;
import br.com.autocenterfiap.ordemservico.domain.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.entity.VeiculoJpaEntity;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.repository.VeiculoJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("OrcamentoController - Testes de Integração")
class OrcamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrcamentoJpaRepository orcamentoJpaRepository;

    @Autowired
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    @Autowired
    private VeiculoJpaRepository veiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private OrcamentoJpaEntity orcamento;

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

        Endereco endereco = new Endereco();
        endereco.setCep("01000-00");
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");

        Cliente cliente = new Cliente();
        cliente.setNome("João da Silva");
        cliente.setTipoCliente(TipoCliente.PESSOA_FISICA);
        cliente.setDocumento("12345678901");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setEndereco(endereco);
        cliente = clienteRepository.saveAndFlush(cliente);

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
        veiculo = veiculoRepository.saveAndFlush(veiculo);

        OrdemServicoJpaEntity ordemServicoJpaEntity = new OrdemServicoJpaEntity();
        ordemServicoJpaEntity.setNumeroOrdemServico(1001L);
        ordemServicoJpaEntity.setStatusOS(StatusOS.AGUARDANDO_APROVACAO);
        ordemServicoJpaEntity.setValorTotal(BigDecimal.valueOf(1000));
        ordemServicoJpaEntity.setCliente(cliente);
        ordemServicoJpaEntity.setVeiculo(veiculo);
        ordemServicoJpaEntity = ordemServicoJpaRepository.saveAndFlush(ordemServicoJpaEntity);

        orcamento = OrcamentoJpaEntity.builder()
                .ordemServicoJpaEntity(ordemServicoJpaEntity)
                .valorTotal(BigDecimal.valueOf(1000))
                .statusOrcamento(StatusOrcamento.AGUARDANDO_APROVACAO)
                .build();
        orcamento = orcamentoJpaRepository.saveAndFlush(orcamento);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve buscar orçamento por ID com sucesso")
    void deveBuscarOrcamentoPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/v1/orcamentos/{id}", orcamento.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orcamento.getId().intValue())))
                .andExpect(jsonPath("$.statusOrcamento", is("AGUARDANDO_APROVACAO")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve retornar 404 ao buscar orçamento inexistente")
    void deveRetornar404AoBuscarOrcamentoInexistente() throws Exception {
        mockMvc.perform(get("/v1/orcamentos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve listar orçamentos por status com paginação")
    void deveListarOrcamentosPorStatus() throws Exception {
        mockMvc.perform(get("/v1/orcamentos")
                        .param("status", "AGUARDANDO_APROVACAO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].statusOrcamento", is("AGUARDANDO_APROVACAO")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve aprovar orçamento com sucesso")
    void deveAprovarOrcamentoComSucesso() throws Exception {
        mockMvc.perform(patch("/v1/orcamentos/{id}/aprovar", orcamento.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusOrcamento", is("APROVADO")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve retornar 404 ao aprovar orçamento inexistente")
    void deveRetornar404AoAprovarOrcamentoInexistente() throws Exception {
        mockMvc.perform(patch("/v1/orcamentos/{id}/aprovar", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve reprovar orçamento com sucesso")
    void deveReprovarOrcamentoComSucesso() throws Exception {
        mockMvc.perform(patch("/v1/orcamentos/{id}/reprovar", orcamento.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusOrcamento", is("REPROVADO")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve retornar 404 ao reprovar orçamento inexistente")
    void deveRetornar404AoReprovarOrcamentoInexistente() throws Exception {
        mockMvc.perform(patch("/v1/orcamentos/{id}/reprovar", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve enviar orçamento para cliente com sucesso")
    void deveEnviarOrcamentoParaCliente() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("tipo", TipoEnvio.EMAIL.name()));

        mockMvc.perform(post("/v1/orcamentos/{id}/enviar-cliente", orcamento.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("enviado via EMAIL")));
    }
}
