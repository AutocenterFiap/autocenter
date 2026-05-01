package br.com.autocenterfiap.ordemservico.controller;

import br.com.autocenterfiap.ordemservico.dto.OSItemServicoRequestDTO;
import br.com.autocenterfiap.ordemservico.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.model.OSItemServico;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.ordemservico.repository.OSItemServicoRepository;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("OSItemServicoController - Testes de Integração")
class OSItemServicoControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OSItemServicoRepository osItemServicoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    private OrdemServico osEmDiagnostico;
    private OrdemServico osEmExecucao;
    private OrdemServico osFinalizada;
    private Servico servicoAtivo;
    private Servico servicoInativo;

    @BeforeEach
    void setUp() {
        osEmDiagnostico = ordemServicoRepository.findAll().stream()
                .filter(os -> os.getNumeroOrdemServico() != null && os.getNumeroOrdemServico() == 1001L)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("OS 1001 não encontrada na migration"));

        osEmExecucao = ordemServicoRepository.findAll().stream()
                .filter(os -> os.getNumeroOrdemServico() != null && os.getNumeroOrdemServico() == 1002L)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("OS 1002 não encontrada na migration"));

        osFinalizada = ordemServicoRepository.findAll().stream()
                .filter(os -> os.getNumeroOrdemServico() != null && os.getNumeroOrdemServico() == 1003L)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("OS 1003 não encontrada na migration"));

        servicoAtivo = servicoRepository.findAll().stream()
                .filter(s -> s.getDescricao().equals("Alinhamento"))
                .findFirst()
                .orElseThrow();

        servicoInativo = servicoRepository.findAll().stream()
                .filter(s -> s.getDescricao().equals("Serviço Inativo"))
                .findFirst()
                .orElseThrow();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET - 200 deve listar todos os serviços de uma OS")
    void deveListarServicosComSucesso() throws Exception {
        mockMvc.perform(get("/v1/api/ordem-servico/{id}/servicos", osEmExecucao.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].valorItemServico", notNullValue()))
                .andExpect(jsonPath("$[0].statusItemServico", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET - 200 deve retornar lista vazia quando OS não tem serviços")
    void deveRetornarListaVaziaQuandoOsNaoTemServicos() throws Exception {
        mockMvc.perform(get("/v1/api/ordem-servico/{id}/servicos", osFinalizada.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST - 201 deve adicionar serviço em OS EM_DIAGNOSTICO")
    void deveAdicionarServicoNaOSComSucesso() throws Exception {
        OSItemServicoRequestDTO request = new OSItemServicoRequestDTO(servicoAtivo.getId());

        mockMvc.perform(post("/v1/api/ordem-servico/{id}/servicos", osEmDiagnostico.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.valorItemServico", is(servicoAtivo.getValor().doubleValue())))
                .andExpect(jsonPath("$.statusItemServico", is("AGUARDANDO_INICIO")))
                .andExpect(jsonPath("$.dataHoraInicio", notNullValue()))
                .andExpect(jsonPath("$.dataHoraFim", nullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST - 400 quando servicoId é nulo")
    void deveRetornar400QuandoServicoIdNulo() throws Exception {
        OSItemServicoRequestDTO requestInvalido = new OSItemServicoRequestDTO(null);

        mockMvc.perform(post("/v1/api/ordem-servico/{id}/servicos", osEmDiagnostico.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST - 400 quando serviço está INATIVO")
    void deveRetornar400QuandoServicoInativo() throws Exception {
        OSItemServicoRequestDTO request = new OSItemServicoRequestDTO(servicoInativo.getId());

        mockMvc.perform(post("/v1/api/ordem-servico/{id}/servicos", osEmDiagnostico.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST - 400 quando OS não está em EM_DIAGNOSTICO")
    void deveRetornar400QuandoOsNaoEstaEmDiagnostico() throws Exception {
        OSItemServicoRequestDTO request = new OSItemServicoRequestDTO(servicoAtivo.getId());

        mockMvc.perform(post("/v1/api/ordem-servico/{id}/servicos", osEmExecucao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST - 404 quando serviço não existe")
    void deveRetornar404QuandoServicoNaoExiste() throws Exception {
        OSItemServicoRequestDTO request = new OSItemServicoRequestDTO(999L);

        mockMvc.perform(post("/v1/api/ordem-servico/{id}/servicos", osEmDiagnostico.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST - 404 quando OS não existe")
    void deveRetornar404QuandoOrdemServicoNaoExiste() throws Exception {
        OSItemServicoRequestDTO request = new OSItemServicoRequestDTO(servicoAtivo.getId());

        mockMvc.perform(post("/v1/api/ordem-servico/{id}/servicos", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /iniciar - 200 deve iniciar serviço AGUARDANDO_INICIO em OS EM_EXECUCAO")
    void deveIniciarServicoComSucesso() throws Exception {
        OSItemServico itemAguardando = osItemServicoRepository
                .findByOrdemServicoId(osEmExecucao.getId()).stream()
                .filter(i -> i.getStatusServico() == StatusItemServico.AGUARDANDO_INICIO)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(patch("/v1/api/ordem-servico/{osId}/servicos/{servicoId}/iniciar",
                        osEmExecucao.getId(), itemAguardando.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemAguardando.getId().intValue())))
                .andExpect(jsonPath("$.statusItemServico", is("EXECUTANDO")))
                .andExpect(jsonPath("$.dataHoraInicio", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /iniciar - 400 quando OS não está em EM_EXECUCAO")
    void deveRetornar400AoIniciarServicoEmOSComStatusInvalido() throws Exception {
        OSItemServico itemDiagnostico = osItemServicoRepository
                .findByOrdemServicoId(osEmDiagnostico.getId()).stream()
                .findFirst()
                .orElseThrow();

        mockMvc.perform(patch("/v1/api/ordem-servico/{osId}/servicos/{servicoId}/iniciar",
                        osEmDiagnostico.getId(), itemDiagnostico.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /iniciar - 400 quando item não está em AGUARDANDO_INICIO")
    void deveRetornar400AoIniciarServicoComStatusItemInvalido() throws Exception {
        OSItemServico itemExecutando = osItemServicoRepository
                .findByOrdemServicoId(osEmExecucao.getId()).stream()
                .filter(i -> i.getStatusServico() == StatusItemServico.EXECUTANDO)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(patch("/v1/api/ordem-servico/{osId}/servicos/{servicoId}/iniciar",
                        osEmExecucao.getId(), itemExecutando.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /iniciar - 404 quando item não existe")
    void deveRetornar404AoIniciarServicoNaoExistente() throws Exception {
        mockMvc.perform(patch("/v1/api/ordem-servico/{osId}/servicos/{servicoId}/iniciar",
                        osEmExecucao.getId(), 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /finalizar - 200 deve finalizar serviço EXECUTANDO")
    void deveFinalizarServicoComSucesso() throws Exception {
        OSItemServico itemExecutando = osItemServicoRepository
                .findByOrdemServicoId(osEmExecucao.getId()).stream()
                .filter(i -> i.getStatusServico() == StatusItemServico.EXECUTANDO)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(patch("/v1/api/ordem-servico/{osId}/servicos/{servicoId}/finalizar",
                        osEmExecucao.getId(), itemExecutando.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemExecutando.getId().intValue())))
                .andExpect(jsonPath("$.statusItemServico", is("FINALIZADO")))
                .andExpect(jsonPath("$.dataHoraFim", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /finalizar - 400 quando item não está em EXECUTANDO")
    void deveRetornar400AoFinalizarServicoComStatusItemInvalido() throws Exception {
        OSItemServico itemAguardando = osItemServicoRepository
                .findByOrdemServicoId(osEmExecucao.getId()).stream()
                .filter(i -> i.getStatusServico() == StatusItemServico.AGUARDANDO_INICIO)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(patch("/v1/api/ordem-servico/{osId}/servicos/{servicoId}/finalizar",
                        osEmExecucao.getId(), itemAguardando.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /finalizar - 404 quando item não existe")
    void deveRetornar404AoFinalizarServicoNaoExistente() throws Exception {
        mockMvc.perform(patch("/v1/api/ordem-servico/{osId}/servicos/{servicoId}/finalizar",
                        osEmExecucao.getId(), 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE - 204 deve remover serviço AGUARDANDO_INICIO em OS EM_DIAGNOSTICO")
    void deveRemoverServicoComSucesso() throws Exception {
        OSItemServico itemAguardando = osItemServicoRepository
                .findByOrdemServicoId(osEmDiagnostico.getId()).stream()
                .findFirst()
                .orElseThrow();

        mockMvc.perform(delete("/v1/api/ordem-servico/{osId}/servicos/{servicoId}",
                        osEmDiagnostico.getId(), itemAguardando.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/api/ordem-servico/{id}/servicos", osEmDiagnostico.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE - 400 quando OS não está em EM_DIAGNOSTICO")
    void deveRetornar400AoRemoverServicoEmOSComStatusInvalido() throws Exception {
        OSItemServico itemExecutando = osItemServicoRepository
                .findByOrdemServicoId(osEmExecucao.getId()).stream()
                .findFirst()
                .orElseThrow();

        mockMvc.perform(delete("/v1/api/ordem-servico/{osId}/servicos/{servicoId}",
                        osEmExecucao.getId(), itemExecutando.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE - 400 quando item não está em AGUARDANDO_INICIO")
    void deveRetornar400AoRemoverServicoComStatusItemInvalido() throws Exception {
        OSItemServico itemDiagnostico = osItemServicoRepository
                .findByOrdemServicoId(osEmDiagnostico.getId()).stream()
                .findFirst()
                .orElseThrow();

        itemDiagnostico.setStatusServico(StatusItemServico.EXECUTANDO);
        osItemServicoRepository.save(itemDiagnostico);

        mockMvc.perform(delete("/v1/api/ordem-servico/{osId}/servicos/{servicoId}",
                        osEmDiagnostico.getId(), itemDiagnostico.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE - 404 quando item não existe")
    void deveRetornar404AoRemoverServicoNaoExistente() throws Exception {
        mockMvc.perform(delete("/v1/api/ordem-servico/{osId}/servicos/{servicoId}",
                        osEmDiagnostico.getId(), 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE - 404 quando OS não existe")
    void deveRetornar404AoRemoverServicoDeOsNaoExistente() throws Exception {
        mockMvc.perform(delete("/v1/api/ordem-servico/{osId}/servicos/{servicoId}",
                        999L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
