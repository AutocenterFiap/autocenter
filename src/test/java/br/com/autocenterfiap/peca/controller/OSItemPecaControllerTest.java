package br.com.autocenterfiap.peca.controller;

import br.com.autocenterfiap.peca.dto.OSItemPecaRequestDTO;
import br.com.autocenterfiap.peca.dto.PecaRequestDTO;
import br.com.autocenterfiap.peca.enums.TipoPeca;
import br.com.autocenterfiap.peca.enums.UnidadeMedida;
import br.com.autocenterfiap.peca.model.OSItemPeca;
import br.com.autocenterfiap.peca.model.Peca;
import br.com.autocenterfiap.peca.repository.OSItemPecaRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("OSItemPecaController - Testes de Integração")
class OSItemPecaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private OSItemPecaRepository osItemPecaRepository;

    private Peca peca;
    private final Long osId = 10L;

    @BeforeEach
    void setUp() {
        osItemPecaRepository.deleteAll();
        pecaRepository.deleteAll();

        PecaRequestDTO dto = new PecaRequestDTO(
                "Filtro de Óleo", "FO-001", null,
                UnidadeMedida.UNIT, new BigDecimal("45.90"),
                50, 10, "Motor", TipoPeca.PECAS
        );
        peca = pecaRepository.save(new Peca(dto));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/ordem-servicos/{osId}/pecas deve adicionar peça e decrementar estoque")
    void deveAdicionarPecaNaOS() throws Exception {
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(peca.getId(), 3);

        mockMvc.perform(post("/v1/ordem-servicos/{osId}/pecas", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.ordemServicoId", is(osId.intValue())))
                .andExpect(jsonPath("$.quantidade", is(3)))
                .andExpect(jsonPath("$.subtotal", is(137.70)));

        Peca pecaAtualizada = pecaRepository.findById(peca.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(47, pecaAtualizada.getQuantidadeEstoque());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/ordem-servicos/{osId}/pecas deve retornar 422 para peça sem estoque")
    void deveRetornar422PorEstoqueInsuficiente() throws Exception {
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(peca.getId(), 999);

        mockMvc.perform(post("/v1/ordem-servicos/{osId}/pecas", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /v1/ordem-servicos/{osId}/pecas deve retornar 422 para peça inativa")
    void deveRetornar422ParaPecaInativa() throws Exception {
        peca.desativar();
        pecaRepository.save(peca);

        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(peca.getId(), 1);

        mockMvc.perform(post("/v1/ordem-servicos/{osId}/pecas", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /v1/ordem-servicos/{osId}/pecas/{pecaId} deve remover e devolver estoque")
    void deveRemoverPecaDaOSEDevolverEstoque() throws Exception {
        OSItemPeca item = new OSItemPeca();
        item.setOrdemServicoId(osId);
        item.setPeca(peca);
        item.setQuantidade(5);
        item.setPrecoUnitarioNoMomento(peca.getPrecoUnitario());
        osItemPecaRepository.save(item);

        peca.setQuantidadeEstoque(45); // simula estoque já decrementado
        pecaRepository.save(peca);

        mockMvc.perform(delete("/v1/ordem-servicos/{osId}/pecas/{pecaId}", osId, peca.getId()))
                .andExpect(status().isNoContent());

        Peca pecaAtualizada = pecaRepository.findById(peca.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(50, pecaAtualizada.getQuantidadeEstoque());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /v1/ordem-servicos/{osId}/pecas deve listar peças da OS")
    void deveListarPecasDaOS() throws Exception {
        OSItemPeca item = new OSItemPeca();
        item.setOrdemServicoId(osId);
        item.setPeca(peca);
        item.setQuantidade(2);
        item.setPrecoUnitarioNoMomento(peca.getPrecoUnitario());
        osItemPecaRepository.save(item);

        mockMvc.perform(get("/v1/ordem-servicos/{osId}/pecas", osId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigoPeca", is("FO-001")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /{pecaId} deve aumentar quantidade e decrementar diferença do estoque")
    void deveAtualizarQuantidadeAumentando() throws Exception {
        // Prepara item com quantidade 3 (estoque = 50 - 3 = 47)
        peca.setQuantidadeEstoque(47);
        pecaRepository.save(peca);

        OSItemPeca item = new OSItemPeca();
        item.setOrdemServicoId(osId);
        item.setPeca(peca);
        item.setQuantidade(3);
        item.setPrecoUnitarioNoMomento(peca.getPrecoUnitario());
        osItemPecaRepository.save(item);

        // Atualiza para quantidade 5 → deve decrementar mais 2 do estoque
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(peca.getId(), 5);

        mockMvc.perform(put("/v1/ordem-servicos/{osId}/pecas/{pecaId}", osId, peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade", is(5)))
                .andExpect(jsonPath("$.subtotal", is(229.50)));

        Peca pecaAtualizada = pecaRepository.findById(peca.getId()).orElseThrow();
        assertEquals(45, pecaAtualizada.getQuantidadeEstoque()); // 47 - 2 = 45
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /{pecaId} deve reduzir quantidade e devolver diferença ao estoque")
    void deveAtualizarQuantidadeReduzindo() throws Exception {
        // Prepara item com quantidade 5 (estoque = 50 - 5 = 45)
        peca.setQuantidadeEstoque(45);
        pecaRepository.save(peca);

        OSItemPeca item = new OSItemPeca();
        item.setOrdemServicoId(osId);
        item.setPeca(peca);
        item.setQuantidade(5);
        item.setPrecoUnitarioNoMomento(peca.getPrecoUnitario());
        osItemPecaRepository.save(item);

        // Atualiza para quantidade 2 → deve devolver 3 ao estoque
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(peca.getId(), 2);

        mockMvc.perform(put("/v1/ordem-servicos/{osId}/pecas/{pecaId}", osId, peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade", is(2)))
                .andExpect(jsonPath("$.subtotal", is(91.80)));

        Peca pecaAtualizada = pecaRepository.findById(peca.getId()).orElseThrow();
        assertEquals(48, pecaAtualizada.getQuantidadeEstoque()); // 45 + 3 = 48
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /{pecaId} deve retornar 422 quando aumento excede o estoque disponível")
    void deveRetornar422AoAtualizarComEstoqueInsuficiente() throws Exception {
        peca.setQuantidadeEstoque(2);
        pecaRepository.save(peca);

        OSItemPeca item = new OSItemPeca();
        item.setOrdemServicoId(osId);
        item.setPeca(peca);
        item.setQuantidade(3);
        item.setPrecoUnitarioNoMomento(peca.getPrecoUnitario());
        osItemPecaRepository.save(item);

        // Tenta aumentar para 100 → estoque tem só 2, insuficiente para mais 97
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(peca.getId(), 100);

        mockMvc.perform(put("/v1/ordem-servicos/{osId}/pecas/{pecaId}", osId, peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST deve preencher dataCriacao no OSItemPeca persistido")
    void devePreencherDataCriacaoAoPersistir() throws Exception {
        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(peca.getId(), 1);

        mockMvc.perform(post("/v1/ordem-servicos/{osId}/pecas", osId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataCriacao", notNullValue()));

        OSItemPeca itemPersistido = osItemPecaRepository.findByOrdemServicoId(osId).get(0);
        assertNotNull(itemPersistido.getDataCriacao(), "dataCriacao deve ser preenchida pelo @PrePersist");
        assertNotNull(itemPersistido.getDataUltimaAtualizacao(), "dataUltimaAtualizacao deve ser preenchida pelo @PrePersist");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT deve atualizar dataUltimaAtualizacao no OSItemPeca após modificação")
    void deveAtualizarDataUltimaAtualizacaoAoModificar() throws Exception {
        OSItemPeca item = new OSItemPeca();
        item.setOrdemServicoId(osId);
        item.setPeca(peca);
        item.setQuantidade(2);
        item.setPrecoUnitarioNoMomento(peca.getPrecoUnitario());
        osItemPecaRepository.save(item);

        assertNotNull(item.getDataUltimaAtualizacao(), "dataUltimaAtualizacao deve ser preenchida pelo @PrePersist");

        peca.setQuantidadeEstoque(48);
        pecaRepository.save(peca);

        OSItemPecaRequestDTO dto = new OSItemPecaRequestDTO(peca.getId(), 4);

        mockMvc.perform(put("/v1/ordem-servicos/{osId}/pecas/{pecaId}", osId, peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        OSItemPeca itemAtualizado = osItemPecaRepository.findByOrdemServicoIdAndPecaId(osId, peca.getId()).orElseThrow();
        assertNotNull(itemAtualizado.getDataUltimaAtualizacao(), "dataUltimaAtualizacao deve ser atualizada pelo @PreUpdate");
    }
}
