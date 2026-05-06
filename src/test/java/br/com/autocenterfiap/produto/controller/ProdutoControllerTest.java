package br.com.autocenterfiap.produto.controller;

import br.com.autocenterfiap.produto.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.produto.dto.ProdutoRequestDTO;
import br.com.autocenterfiap.produto.enums.TipoProduto;
import br.com.autocenterfiap.produto.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.model.Produto;
import br.com.autocenterfiap.produto.repository.ProdutoRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProdutoController - Testes de Integração")
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    private ProdutoRequestDTO requestDTO;
    private Produto produtoSalvo;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();
        produtoRepository.flush();

        requestDTO = new ProdutoRequestDTO(
                "Filtro de Óleo",
                "FO-001",
                "Filtro para motores 1.0 a 2.0",
                UnidadeMedida.UNIT,
                new BigDecimal("45.90"),
                100,
                10,
                "Motor",
                TipoProduto.PECAS
        );

        Produto produto = new Produto(requestDTO);
        produtoSalvo = produtoRepository.save(produto);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/produtos deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() throws Exception {
        ProdutoRequestDTO novaDto = new ProdutoRequestDTO(
                "Pastilha de Freio",
                "PF-002",
                "Pastilha dianteira",
                UnidadeMedida.UNIT,
                new BigDecimal("89.00"),
                50,
                5,
                "Freios",
                TipoProduto.PECAS
        );

        mockMvc.perform(post("/v1/produtos")
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
    @DisplayName("POST /v1/produtos deve retornar 409 quando código já cadastrado")
    void deveRetornar409AoCriarComCodigoDuplicado() throws Exception {
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/produtos deve retornar 400 para dados inválidos")
    void deveRetornar400ParaDadosInvalidos() throws Exception {
        ProdutoRequestDTO invalido = new ProdutoRequestDTO(
                "",         // nome vazio
                "XX-001",
                null,
                UnidadeMedida.UNIT,
                new BigDecimal("-5.00"),  // preço negativo
                100,
                10,
                "Motor",
                TipoProduto.PECAS
        );

        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/produtos deve listar produtos ativos")
    void deveListarProdutos() throws Exception {
        mockMvc.perform(get("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo", is("FO-001")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/produtos/{id} deve retornar produto por ID")
    void deveBuscarPorId() throws Exception {
        mockMvc.perform(get("/v1/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("FO-001")))
                .andExpect(jsonPath("$.statusEstoque", is("NORMAL")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/produtos/{id} deve retornar 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        mockMvc.perform(get("/v1/produtos/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /v1/produtos/{id}/estoque/adicionar deve incrementar estoque")
    void deveAdicionarEstoque() throws Exception {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(50, "Reposição");

        mockMvc.perform(patch("/v1/produtos/{id}/estoque/adicionar", produtoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque", is(150)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /v1/produtos/{id}/estoque/remover deve decrementar estoque")
    void deveRemoverEstoque() throws Exception {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO(30, "Saída manual");

        mockMvc.perform(patch("/v1/produtos/{id}/estoque/remover", produtoSalvo.getId())
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

        mockMvc.perform(patch("/v1/produtos/{id}/estoque/remover", produtoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /v1/produtos/{id} deve desativar produto (soft delete)")
    void deveDesativarProduto() throws Exception {
        mockMvc.perform(delete("/v1/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/produtos/estoque/alertas deve listar produtos com problema de estoque")
    void deveListarAlertasEstoque() throws Exception {
        produtoSalvo.setQuantidadeEstoque(3);
        produtoRepository.save(produtoSalvo);

        mockMvc.perform(get("/v1/produtos/estoque/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].statusEstoque", is("LOW_STOCK")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /v1/produtos/{id} deve atualizar produto com sucesso")
    void deveAtualizarProduto() throws Exception {
        ProdutoRequestDTO atualizado = new ProdutoRequestDTO(
                "Filtro de Óleo Premium",
                "FO-001",
                "Filtro premium para motores",
                UnidadeMedida.UNIT,
                new BigDecimal("55.00"),
                100,
                15,
                "Motor",
                TipoProduto.PECAS
        );

        mockMvc.perform(put("/v1/produtos/{id}", produtoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Filtro de Óleo Premium")))
                .andExpect(jsonPath("$.estoqueMinimo", is(15)));
    }
}
