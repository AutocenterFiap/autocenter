package br.com.autocenterfiap.orcamento.controller;

import br.com.autocenterfiap.orcamento.enums.StatusOrcamento;
import br.com.autocenterfiap.orcamento.repository.entity.Orcamento;
import br.com.autocenterfiap.orcamento.service.OrcamentoService;
import br.com.autocenterfiap.ordemservico.repository.entity.OrdemServico;
import br.com.autocenterfiap.security.repository.UsuarioRepository;
import br.com.autocenterfiap.security.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrcamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrcamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrcamentoService orcamentoService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    void deveAprovarOrcamento() throws Exception {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        Orcamento orcamento = Orcamento.builder()
                .id(1L)
                .ordemServico(os)
                .valorTotal(BigDecimal.valueOf(1200))
                .statusOrcamento(StatusOrcamento.APROVADO)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .build();

        when(orcamentoService.aprovar(1L)).thenReturn(orcamento);

        mockMvc.perform(patch("/v1/orcamentos/1/aprovar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusOrcamento").value("APROVADO"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveReprovarOrcamento() throws Exception {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        Orcamento orcamento = Orcamento.builder()
                .id(2L)
                .ordemServico(os)
                .valorTotal(BigDecimal.valueOf(800))
                .statusOrcamento(StatusOrcamento.REPROVADO)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .build();

        when(orcamentoService.reprovar(2L)).thenReturn(orcamento);

        mockMvc.perform(patch("/v1/orcamentos/2/reprovar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusOrcamento").value("REPROVADO"))
                .andExpect(jsonPath("$.id").value(2));
    }
}
