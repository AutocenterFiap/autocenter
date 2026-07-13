package br.com.autocenterfiap.ordemservico.controller;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.model.Endereco;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoDTO;
import br.com.autocenterfiap.ordemservico.adapter.in.dto.OrdemServicoUpdateDTO;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("OrdemServicoController - Testes de Integração")
class OrdemServicoJpaEntityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrdemServicoJpaRepository repository;

    @Autowired
    private VeiculoJpaRepository veiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente;
    private VeiculoJpaEntity veiculo;
    private OrdemServicoJpaEntity ordemServicoJpaEntity;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.flush();
        veiculoRepository.deleteAll();
        veiculoRepository.flush();
        clienteRepository.deleteAll();
        clienteRepository.flush();

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
        cliente = clienteRepository.save(cliente);

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
        veiculo = veiculoRepository.save(veiculo);

        ordemServicoJpaEntity = new OrdemServicoJpaEntity();
        ordemServicoJpaEntity.setNumeroOrdemServico(1001L);
        ordemServicoJpaEntity.setStatusOS(StatusOS.ABERTA);
        ordemServicoJpaEntity.setValorTotal(BigDecimal.ZERO);
        ordemServicoJpaEntity.setVeiculo(veiculo);
        ordemServicoJpaEntity.setCliente(cliente);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarTodasAsOrdensDeServico() throws Exception {
        repository.save(ordemServicoJpaEntity);

        mockMvc.perform(get("/v1/ordem-servicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].numeroOrdemServico", is(1001)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornarListaVaziaQuandoNaoHouverOrdensDeServico() throws Exception {
        mockMvc.perform(get("/v1/ordem-servicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarOrdemServicoPorIdComSucesso() throws Exception {
        OrdemServicoJpaEntity saved = repository.save(ordemServicoJpaEntity);

        mockMvc.perform(get("/v1/ordem-servicos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.numeroOrdemServico", is(1001)))
                .andExpect(jsonPath("$.statusOS", is("ABERTA")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoBuscarOrdemServicoInexistentePorId() throws Exception {
        mockMvc.perform(get("/v1/ordem-servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarOrdemServicoPorNumeroComSucesso() throws Exception {
        repository.save(ordemServicoJpaEntity);

        mockMvc.perform(get("/v1/ordem-servicos/numero/{numeroOs}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroOrdemServico", is(1001)))
                .andExpect(jsonPath("$.statusOS", is("ABERTA")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoBuscarOrdemServicoInexistentePorNumero() throws Exception {
        mockMvc.perform(get("/v1/ordem-servicos/numero/{numeroOs}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarOrdemServicoComSucesso() throws Exception {
        OrdemServicoDTO dto = new OrdemServicoDTO(veiculo.getId(), cliente.getId());
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/v1/ordem-servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.veiculoId", is(veiculo.getId().intValue())))
                .andExpect(jsonPath("$.clienteId", is(cliente.getId().intValue())))
                .andExpect(jsonPath("$.statusOS", is("ABERTA")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarOrdemServicoComDadosInvalidos() throws Exception {
        OrdemServicoDTO dto = new OrdemServicoDTO(null, null);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/v1/ordem-servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAtualizarStatusOrdemServicoComSucesso() throws Exception {
        OrdemServicoJpaEntity saved = repository.save(ordemServicoJpaEntity);

        OrdemServicoUpdateDTO dto = new OrdemServicoUpdateDTO(StatusOS.RECEBIDA);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/v1/ordem-servicos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.statusOS", is("RECEBIDA")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoTentarAtualizarComStatusInvalido() throws Exception {
        OrdemServicoJpaEntity saved = repository.save(ordemServicoJpaEntity);

        OrdemServicoUpdateDTO dto = new OrdemServicoUpdateDTO(StatusOS.AGUARDANDO_APROVACAO);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/v1/ordem-servicos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoAtualizarOrdemServicoInexistente() throws Exception {
        OrdemServicoUpdateDTO dto = new OrdemServicoUpdateDTO(StatusOS.RECEBIDA);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/v1/ordem-servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveDeletarOrdemServicoComSucesso() throws Exception {
        OrdemServicoJpaEntity saved = repository.save(ordemServicoJpaEntity);

        mockMvc.perform(delete("/v1/ordem-servicos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/ordem-servicos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoDeletarOrdemServicoInexistente() throws Exception {
        mockMvc.perform(delete("/v1/ordem-servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
