package br.com.autocenterfiap.servico.controller;

import br.com.autocenterfiap.servico.enums.StatusServico;
import br.com.autocenterfiap.servico.model.Servico;
import br.com.autocenterfiap.servico.repository.ServicoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("ServicoController - Testes de Integração")
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServicoRepository servicoRepository;

    private Servico servico;

    @BeforeEach
    void setUp() {
        servicoRepository.deleteAll();
        servico = new Servico();
        servico.setDescricao("Troca de óleo");
        servico.setStatus(StatusServico.ATIVO);
        servico.setValor(BigDecimal.valueOf(100));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarTodosOsServicos() throws Exception {
        servicoRepository.save(servico);
        mockMvc.perform(get("/v1/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].descricao", is("Troca de óleo")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornarListaVaziaQuandoNaoHouverServicos() throws Exception {
        mockMvc.perform(get("/v1/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarServicoPorIdComSucesso() throws Exception {
        Servico servicoSalvo = servicoRepository.save(servico);
        mockMvc.perform(get("/v1/api/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(servicoSalvo.getId().intValue())))
                .andExpect(jsonPath("$.descricao", is("Troca de óleo")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoBuscarServicoInexistentePorId() throws Exception {
        mockMvc.perform(get("/v1/api/servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarServicoComSucesso() throws Exception {
        mockMvc.perform(post("/v1/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servico)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.descricao", is("Troca de óleo")))
                .andExpect(jsonPath("$.status", is("ATIVO")))
                .andExpect(jsonPath("$.valor", is(100)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarServicoComDadosInvalidos() throws Exception {
        Servico servicoInvalido = new Servico();
        servicoInvalido.setDescricao(""); // descrição vazia
        mockMvc.perform(post("/v1/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAtualizarServicoComSucesso() throws Exception {
        Servico servicoSalvo = servicoRepository.save(servico);
        servicoSalvo.setDescricao("Troca de filtro");
        mockMvc.perform(put("/v1/api/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoSalvo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", is("Troca de filtro")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoAtualizarServicoInexistente() throws Exception {
        mockMvc.perform(put("/v1/api/servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servico)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveDeletarServicoComSucesso() throws Exception {
        Servico servicoSalvo = servicoRepository.save(servico);
        mockMvc.perform(delete("/v1/api/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/api/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoDeletarServicoInexistente() throws Exception {
        mockMvc.perform(delete("/v1/api/servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

