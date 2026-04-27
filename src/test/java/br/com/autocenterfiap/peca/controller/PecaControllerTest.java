package br.com.autocenterfiap.peca.controller;

import br.com.autocenterfiap.peca.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.peca.dto.PecaRequestDTO;
import br.com.autocenterfiap.peca.enums.TipoPeca;
import br.com.autocenterfiap.peca.enums.UnidadeMedida;
import br.com.autocenterfiap.peca.model.Peca;
import br.com.autocenterfiap.peca.repository.PecaRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("PecaController - Testes de Integração")
class PecaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PecaRepository pecaRepository;

    private PecaRequestDTO requestDTO;
    private Peca pecaSalva;

    @BeforeEach
    void setUp() {
        pecaRepository.deleteAll();

        requestDTO = new PecaRequestDTO(
                "Filtro de Óleo",
                "FO-001",
                "Filtro para motores 1.0 a 2.0",
                UnidadeMedida.UNIT,
                new BigDecimal("45.90"),
                100,
                10,
                "Motor",
                TipoPeca.PECAS
        );

        Peca peca = new Peca(requestDTO);
        pecaSalva = pecaRepository.save(peca);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/api/pecas deve criar peça com sucesso")
    void deveCriarPecaComSucesso() throws Exception {
        PecaRequestDTO novaDto = new PecaRequestDTO(
                "Pastilha de Freio",
                "PF-002",
                "Pastilha dianteira",
                UnidadeMedida.UNIT,
                new BigDecimal("89.00"),
                50,
                5,
                "Freios",
                TipoPeca.PECAS
        );

        mockMvc.perform(post("/v1/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.codigo", is("PF-002")))
                .andExpect(jsonPath("$.nome", is("Pastilha de Freio")))
                .andExpect(jsonPath("$.quantidadeEstoque", is(50)))
                .andExpect(jsonPath("$.ativo", is(true)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/api/pecas deve retornar 409 quando código já cadastrado")
    void deveRetornar409AoCriarComCodigoDuplicado() throws Exception {
        mockMvc.perform(post("/v1/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/api/pecas deve retornar 400 para dados inválidos")
    void deveRetornar400ParaDadosInvalidos() throws Exception {
        PecaRequestDTO invalido = new PecaRequestDTO(
                "",         // nome vazio
                "XX-001",
                null,
                UnidadeMedida.UNIT,
                new BigDecimal("-5.00"),  // preço negativo
                100,
                10,
                "Motor",
                TipoPeca.PECAS
        );

        mockMvc.perform(post("/v1/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/api/pecas deve listar peças ativas")
    void deveListarPecas() throws Exception {
        mockMvc.perform(get("/v1/api/pecas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo", is("FO-001")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/api/pecas/{id} deve retornar peça por ID")
    void deveBuscarPorId() throws Exception {
        mockMvc.perform(get("/v1/api/pecas/{id}", pecaSalva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("FO-001")))
                .andExpect(jsonPath("$.statusEstoque", is("NORMAL")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/api/pecas/{id} deve retornar 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        mockMvc.perform(get("/v1/api/pecas/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /v1/api/pecas/{id}/estoque/adicionar deve incrementar estoque")
    void deveAdicionarEstoque() throws Exception {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(50, "Reposição");

        mockMvc.perform(patch("/v1/api/pecas/{id}/estoque/adicionar", pecaSalva.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque", is(150)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /v1/api/pecas/{id}/estoque/remover deve decrementar estoque")
    void deveRemoverEstoque() throws Exception {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(30, "Saída manual");

        mockMvc.perform(patch("/v1/api/pecas/{id}/estoque/remover", pecaSalva.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque", is(70)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH estoque/remover deve retornar 422 quando estoque insuficiente")
    void deveRetornar422AoRemoverComEstoqueInsuficiente() throws Exception {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(999, "Tentativa inválida");

        mockMvc.perform(patch("/v1/api/pecas/{id}/estoque/remover", pecaSalva.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /v1/api/pecas/{id} deve desativar peça (soft delete)")
    void deveDesativarPeca() throws Exception {
        mockMvc.perform(delete("/v1/api/pecas/{id}", pecaSalva.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/api/pecas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/api/pecas/estoque/alertas deve listar peças com problema de estoque")
    void deveListarAlertasEstoque() throws Exception {
        pecaSalva.setQuantidadeEstoque(3);
        pecaRepository.save(pecaSalva);

        mockMvc.perform(get("/v1/api/pecas/estoque/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].statusEstoque", is("LOW_STOCK")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /v1/api/pecas/{id} deve atualizar peça com sucesso")
    void deveAtualizarPeca() throws Exception {
        PecaRequestDTO atualizado = new PecaRequestDTO(
                "Filtro de Óleo Premium",
                "FO-001",
                "Filtro premium para motores",
                UnidadeMedida.UNIT,
                new BigDecimal("55.00"),
                100,
                15,
                "Motor",
                TipoPeca.PECAS
        );

        mockMvc.perform(put("/v1/api/pecas/{id}", pecaSalva.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Filtro de Óleo Premium")))
                .andExpect(jsonPath("$.estoqueMinimo", is(15)));
    }
}
