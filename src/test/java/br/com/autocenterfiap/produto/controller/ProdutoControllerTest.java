package br.com.autocenterfiap.produto.controller;

import br.com.autocenterfiap.produto.adapter.in.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.produto.adapter.in.dto.ProdutoRequestDTO;
import br.com.autocenterfiap.produto.domain.enums.TipoProduto;
import br.com.autocenterfiap.produto.domain.enums.UnidadeMedida;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.entity.ProdutoJpaEntity;
import br.com.autocenterfiap.produto.infrastructure.persistence.jpa.repository.ProdutoJpaRepository;
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
    private ProdutoJpaRepository produtoRepository;

    private ProdutoJpaEntity produto;
    private ProdutoRequestDTO produtoRequestDTO;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();
        produtoRepository.flush();

        produto = ProdutoJpaEntity.builder()
                .nome("Filtro de Ar")
                .codigo("FA-001")
                .descricao("Filtro de ar esportivo")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(89.90))
                .quantidadeEstoque(20)
                .estoqueMinimo(5)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .ativo(true)
                .build();

        produtoRequestDTO = new ProdutoRequestDTO(
                "Filtro de Ar",
                "FA-001",
                "Filtro de ar esportivo",
                UnidadeMedida.UNIT,
                BigDecimal.valueOf(89.90),
                20,
                5,
                "Filtros",
                TipoProduto.PECAS
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve listar todos os produtos ativos")
    void deveListarTodosOsProdutos() throws Exception {
        produtoRepository.save(produto);
        mockMvc.perform(get("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo", is("FA-001")))
                .andExpect(jsonPath("$[0].nome", is("Filtro de Ar")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() throws Exception {
        ProdutoJpaEntity salvo = produtoRepository.save(produto);
        mockMvc.perform(get("/v1/produtos/{id}", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(salvo.getId().intValue())))
                .andExpect(jsonPath("$.codigo", is("FA-001")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve retornar 404 ao buscar produto inexistente por ID")
    void deveRetornar404AoBuscarInexistente() throws Exception {
        mockMvc.perform(get("/v1/produtos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() throws Exception {
        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.codigo", is("FA-001")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve retornar 409 ao criar produto com código duplicado")
    void deveRetornar409AoCriarDuplicado() throws Exception {
        produtoRepository.save(produto);

        mockMvc.perform(post("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoRequestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() throws Exception {
        ProdutoJpaEntity salvo = produtoRepository.save(produto);

        ProdutoRequestDTO updateDto = new ProdutoRequestDTO(
                "Filtro de Ar Premium",
                "FA-001",
                "Premium",
                UnidadeMedida.UNIT,
                BigDecimal.valueOf(99.90),
                10,
                3,
                "Filtros",
                TipoProduto.PECAS
        );

        mockMvc.perform(put("/v1/produtos/{id}", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Filtro de Ar Premium")))
                .andExpect(jsonPath("$.precoUnitario", is(99.90)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve desativar produto com sucesso")
    void deveDesativarProdutoComSucesso() throws Exception {
        ProdutoJpaEntity salvo = produtoRepository.save(produto);

        mockMvc.perform(delete("/v1/produtos/{id}", salvo.getId()))
                .andExpect(status().isNoContent());

        // A listagem padrão só retorna ativos, então deve retornar vazia
        mockMvc.perform(get("/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve adicionar estoque com sucesso")
    void deveAdicionarEstoqueComSucesso() throws Exception {
        ProdutoJpaEntity salvo = produtoRepository.save(produto);
        MovimentacaoEstoqueDTO mov = new MovimentacaoEstoqueDTO(10, "Reposição");

        mockMvc.perform(patch("/v1/produtos/{id}/estoque/adicionar", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque", is(30)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve remover estoque com sucesso")
    void deveRemoverEstoqueComSucesso() throws Exception {
        ProdutoJpaEntity salvo = produtoRepository.save(produto);
        MovimentacaoEstoqueDTO mov = new MovimentacaoEstoqueDTO(10, "Saída manual");

        mockMvc.perform(patch("/v1/produtos/{id}/estoque/remover", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque", is(10)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve retornar 422 ao remover quantidade maior que estoque disponível")
    void deveRetornar422AoRemoverEstoqueInsuficiente() throws Exception {
        ProdutoJpaEntity salvo = produtoRepository.save(produto);
        MovimentacaoEstoqueDTO mov = new MovimentacaoEstoqueDTO(30, "Saída manual"); // estoque tem 20

        mockMvc.perform(patch("/v1/produtos/{id}/estoque/remover", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deve retornar alertas de estoque baixo ou sem estoque")
    void deveListarAlertasEstoque() throws Exception {
        // produto tem 20 estoque, minimo 5 -> normal (sem alerta)
        produtoRepository.save(produto);

        // produto sem estoque
        ProdutoJpaEntity semEstoque = ProdutoJpaEntity.builder()
                .nome("Filtro de Óleo")
                .codigo("FO-001")
                .unidadeMedida(UnidadeMedida.UNIT)
                .precoUnitario(BigDecimal.valueOf(45.90))
                .quantidadeEstoque(0)
                .estoqueMinimo(5)
                .categoria("Filtros")
                .tipo(TipoProduto.PECAS)
                .ativo(true)
                .build();
        produtoRepository.save(semEstoque);

        mockMvc.perform(get("/v1/produtos/estoque/alertas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo", is("FO-001")));
    }
}
