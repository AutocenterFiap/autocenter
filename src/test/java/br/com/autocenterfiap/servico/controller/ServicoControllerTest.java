package br.com.autocenterfiap.servico.controller;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity.OSItemServicoJpaEntity;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OSItemServicoJpaRepository;
import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import br.com.autocenterfiap.servico.adapter.in.dto.ServicoRequestDTO;
import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.entity.ServicoJpaEntity;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.repository.ServicoJpaRepository;
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
@DisplayName("ServicoController - Testes de Integração")
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServicoJpaRepository servicoRepository;

    @Autowired
    private OSItemServicoJpaRepository osItemServicoJpaRepository;

    @Autowired
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    private ServicoJpaEntity servico;

    private ServicoRequestDTO servicoDto;

    @BeforeEach
    void setUp() {
        osItemServicoJpaRepository.deleteAll();
        osItemServicoJpaRepository.flush();
        servicoRepository.deleteAll();
        servicoRepository.flush();
        
        servico = ServicoJpaEntity.builder()
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100))
                .build();

        servicoDto = ServicoRequestDTO.builder()
                .descricao("Troca de óleo")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarTodosOsServicos() throws Exception {
        servicoRepository.save(servico);
        mockMvc.perform(get("/v1/servicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].descricao", is("Troca de óleo")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornarListaVaziaQuandoNaoHouverServicos() throws Exception {
        mockMvc.perform(get("/v1/servicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarServicoPorIdComSucesso() throws Exception {
        ServicoJpaEntity servicoSalvo = servicoRepository.save(servico);
        mockMvc.perform(get("/v1/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(servicoSalvo.getId().intValue())))
                .andExpect(jsonPath("$.descricao", is("Troca de óleo")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoBuscarServicoInexistentePorId() throws Exception {
        mockMvc.perform(get("/v1/servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarServicosPorStatusQuandoExistirem() throws Exception {
        ServicoJpaEntity outro = ServicoJpaEntity.builder()
                .descricao("Alinhamento")
                .status(StatusServico.INATIVO)
                .valor(BigDecimal.valueOf(120))
                .build();

        servicoRepository.save(servico);
        servicoRepository.save(outro);

        mockMvc.perform(get("/v1/servicos/status/{status}", StatusServico.ATIVO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].descricao", is("Troca de óleo")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornarListaVaziaAoListarPorStatusQuandoNaoHouver() throws Exception {
        servicoRepository.save(servico);

        mockMvc.perform(get("/v1/servicos/status/{status}", StatusServico.INATIVO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarServicoComSucesso() throws Exception {
        mockMvc.perform(post("/v1/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.descricao", is("Troca de óleo")))
                .andExpect(jsonPath("$.status", is("ATIVO")))
                .andExpect(jsonPath("$.valor", is(100)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarServicoComDadosInvalidos() throws Exception {
        ServicoRequestDTO servicoInvalido = ServicoRequestDTO.builder()
                .descricao("") // descrição vazia
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100))
                .build();
        mockMvc.perform(post("/v1/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAtualizarServicoComSucesso() throws Exception {
        ServicoJpaEntity servicoSalvo = servicoRepository.save(servico);
        
        ServicoRequestDTO updateDto = ServicoRequestDTO.builder()
                .descricao("Troca de filtro")
                .status(StatusServico.ATIVO)
                .valor(BigDecimal.valueOf(100))
                .build();
                
        mockMvc.perform(put("/v1/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", is("Troca de filtro")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoAtualizarServicoInexistente() throws Exception {
        mockMvc.perform(put("/v1/servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicoDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveDeletarServicoComSucesso() throws Exception {
        ServicoJpaEntity servicoSalvo = servicoRepository.save(servico);
        mockMvc.perform(delete("/v1/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoDeletarServicoInexistente() throws Exception {
        mockMvc.perform(delete("/v1/servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoDeletarServicoEmUso() throws Exception {
        ServicoJpaEntity servicoSalvo = servicoRepository.save(servico);
        servicoRepository.flush();

        OrdemServicoJpaEntity os = ordemServicoJpaRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhuma OrdemServico encontrada para associar"));

        OSItemServicoJpaEntity item = new OSItemServicoJpaEntity();
        item.setOrdemServicoJpaEntity(os);
        item.setServico(servicoSalvo);
        item.setValorItemServico(BigDecimal.valueOf(100));
        item.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);
        osItemServicoJpaRepository.save(item);
        osItemServicoJpaRepository.flush();

        mockMvc.perform(delete("/v1/servicos/{id}", servicoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro", is("Conflito de Dados")))
                .andExpect(jsonPath("$.mensagem", containsString("Não é possível deletar o serviço")));
    }
}
