package br.com.autocenterfiap.ordemservico.controller;

import br.com.autocenterfiap.ordemservico.model.OSItemProduto;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.ordemservico.repository.OSItemProdutoRepository;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.produto.dto.OSItemProdutoRequestDTO;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("OSItemProdutoController - Testes de Integração")
class OSItemProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private OSItemProdutoRepository osItemProdutoRepository;

    private Produto produto;
    private Long osId;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        ordemServicoRepository.deleteAll();
        ordemServicoRepository.flush();
        osItemProdutoRepository.deleteAll();
        osItemProdutoRepository.flush();
        produtoRepository.deleteAll();
        produtoRepository.flush();

        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Filtro de Óleo", "FO-001", null,
                UnidadeMedida.UNIT, new BigDecimal("45.90"),
                50, 10, "Motor", TipoProduto.PECAS
        );
        produto = produtoRepository.save(new Produto(dto));

        ordemServico = ordemServicoRepository.save(new OrdemServico());
        osId = ordemServico.getId(); 
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/ordem-servicos/{osId}/produtos deve adicionar produto e decrementar estoque")
    void deveAdicionarProdutoNaOS() throws Exception {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(produto.getId(), 3);

        mockMvc.perform(post("/v1/ordem-servicos/{osId}/produtos", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.ordemServicoId", is(osId.intValue())))
                .andExpect(jsonPath("$.quantidade", is(3)))
                .andExpect(jsonPath("$.subtotal", is(137.70)));

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(47, produtoAtualizado.getQuantidadeEstoque());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/ordem-servicos/{osId}/produtos deve retornar 422 para produto sem estoque")
    void deveRetornar422PorEstoqueInsuficiente() throws Exception {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(produto.getId(), 999);

        mockMvc.perform(post("/v1/ordem-servicos/{osId}/produtos", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/ordem-servicos/{osId}/produtos deve retornar 422 para produto inativo")
    void deveRetornar422ParaProdutoInativo() throws Exception {
        produto.desativar();
        produtoRepository.save(produto);

        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(produto.getId(), 1);

        mockMvc.perform(post("/v1/ordem-servicos/{osId}/produtos", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /v1/ordem-servicos/{osId}/produtos/{produtoId} deve remover e devolver estoque")
    void deveRemoverProdutoDaOSEDevolverEstoque() throws Exception {
        OSItemProduto item = new OSItemProduto();
        item.setOrdemServico(ordemServico);
        item.setProduto(produto);
        item.setQuantidade(5);
        item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());
        osItemProdutoRepository.save(item);

        produto.setQuantidadeEstoque(45); // simula estoque já decrementado
        produtoRepository.save(produto);

        mockMvc.perform(delete("/v1/ordem-servicos/{osId}/produtos/{produtoId}", osId, produto.getId()))
                .andExpect(status().isNoContent());

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(50, produtoAtualizado.getQuantidadeEstoque());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/ordem-servicos/{osId}/produtos deve listar produtos da OS")
    void deveListarProdutosDaOS() throws Exception {
        OSItemProduto item = new OSItemProduto();
        item.setOrdemServico(ordemServico);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());
        osItemProdutoRepository.save(item);

        mockMvc.perform(get("/v1/ordem-servicos/{osId}/produtos", osId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigoProduto", is("FO-001")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /{produtoId} deve aumentar quantidade e decrementar diferença do estoque")
    void deveAtualizarQuantidadeAumentando() throws Exception {
        // Prepara item com quantidade 3 (estoque = 50 - 3 = 47)
        produto.setQuantidadeEstoque(47);
        produtoRepository.save(produto);

        OSItemProduto item = new OSItemProduto();
        item.setOrdemServico(ordemServico);
        item.setProduto(produto);
        item.setQuantidade(3);
        item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());
        osItemProdutoRepository.save(item);

        // Atualiza para quantidade 5 → deve decrementar mais 2 do estoque
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(produto.getId(), 5);

        mockMvc.perform(put("/v1/ordem-servicos/{osId}/produtos/{produtoId}", osId, produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade", is(5)))
                .andExpect(jsonPath("$.subtotal", is(229.50)));

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(45, produtoAtualizado.getQuantidadeEstoque()); // 47 - 2 = 45
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /{produtoId} deve reduzir quantidade e devolver diferença ao estoque")
    void deveAtualizarQuantidadeReduzindo() throws Exception {
        // Prepara item com quantidade 5 (estoque = 50 - 5 = 45)
        produto.setQuantidadeEstoque(45);
        produtoRepository.save(produto);

        OSItemProduto item = new OSItemProduto();
        item.setOrdemServico(ordemServico);
        item.setProduto(produto);
        item.setQuantidade(5);
        item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());
        osItemProdutoRepository.save(item);

        // Atualiza para quantidade 2 → deve devolver 3 ao estoque
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(produto.getId(), 2);

        mockMvc.perform(put("/v1/ordem-servicos/{osId}/produtos/{produtoId}", osId, produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade", is(2)))
                .andExpect(jsonPath("$.subtotal", is(91.80)));

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(48, produtoAtualizado.getQuantidadeEstoque()); // 45 + 3 = 48
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /{produtoId} deve retornar 422 quando aumento excede o estoque disponível")
    void deveRetornar422AoAtualizarComEstoqueInsuficiente() throws Exception {
        produto.setQuantidadeEstoque(2);
        produtoRepository.save(produto);

        OSItemProduto item = new OSItemProduto();
        item.setOrdemServico(ordemServico);
        item.setProduto(produto);
        item.setQuantidade(3);
        item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());
        osItemProdutoRepository.save(item);

        // Tenta aumentar para 100 → estoque tem só 2, insuficiente para mais 97
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(produto.getId(), 100);

        mockMvc.perform(put("/v1/ordem-servicos/{osId}/produtos/{produtoId}", osId, produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST deve preencher dataCriacao no OSItemProduto persistido")
    void devePreencherDataCriacaoAoPersistir() throws Exception {
        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(produto.getId(), 1);

        mockMvc.perform(post("/v1/ordem-servicos/{osId}/produtos", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataCriacao", notNullValue()));

        OSItemProduto itemPersistido = osItemProdutoRepository.findByOrdemServicoId(osId).get(0);
        assertNotNull(itemPersistido.getDataCriacao(), "dataCriacao deve ser preenchida pelo @PrePersist");
        assertNotNull(itemPersistido.getDataUltimaAtualizacao(), "dataUltimaAtualizacao deve ser preenchida pelo @PrePersist");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT deve atualizar dataUltimaAtualizacao no OSItemProduto após modificação")
    void deveAtualizarDataUltimaAtualizacaoAoModificar() throws Exception {
        OSItemProduto item = new OSItemProduto();
        item.setOrdemServico(ordemServico);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitarioNoMomento(produto.getPrecoUnitario());
        osItemProdutoRepository.save(item);

        assertNotNull(item.getDataUltimaAtualizacao(), "dataUltimaAtualizacao deve ser preenchida pelo @PrePersist");

        produto.setQuantidadeEstoque(48);
        produtoRepository.save(produto);

        OSItemProdutoRequestDTO dto = new OSItemProdutoRequestDTO(produto.getId(), 4);

        mockMvc.perform(put("/v1/ordem-servicos/{osId}/produtos/{produtoId}", osId, produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        OSItemProduto itemAtualizado = osItemProdutoRepository.findByOrdemServicoIdAndProdutoId(osId, produto.getId()).orElseThrow();
        assertNotNull(itemAtualizado.getDataUltimaAtualizacao(), "dataUltimaAtualizacao deve ser atualizada pelo @PreUpdate");
    }
}
