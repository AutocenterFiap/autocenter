package br.com.autocenterfiap.veiculo.controller;

import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import br.com.autocenterfiap.veiculo.adapter.in.dto.VeiculoRequestDTO;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("VeiculoController - Testes de Integração")
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VeiculoJpaRepository repository;

    @Autowired
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    private VeiculoJpaEntity veiculo;
    private VeiculoJpaEntity veiculoSegundo;

    @BeforeEach
    void setUp() {
        ordemServicoJpaRepository.deleteAll();
        ordemServicoJpaRepository.flush();
        repository.deleteAll();
        repository.flush();

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

        veiculoSegundo = new VeiculoJpaEntity();
        veiculoSegundo.setPlaca("ABC1D26");
        veiculoSegundo.setChassi("8BWZZZ377VT004251");
        veiculoSegundo.setRenavam("23022215548");
        veiculoSegundo.setMarca("Kawasaki");
        veiculoSegundo.setModelo("Ninja");
        veiculoSegundo.setAnoFabricacao(2026);
        veiculoSegundo.setAnoModelo(2026);
        veiculoSegundo.setCor("Verde");
        veiculoSegundo.setQuilometragem(0L);
        veiculoSegundo.setTipoCombustivel(TipoCombustivel.GASOLINA);
        veiculoSegundo.setCategoriaVeiculo(CategoriaVeiculo.MOTO);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarTodosOsVeiculos() throws Exception {
        repository.save(veiculo);

        mockMvc.perform(get("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].placa", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornarListaVaziaQuandoNaoHouverVeiculos() throws Exception {
        mockMvc.perform(get("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarVeiculoPorIdComSucesso() throws Exception {
        VeiculoJpaEntity saved = repository.save(veiculo);

        mockMvc.perform(get("/v1/veiculos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(veiculo.getId().intValue())))
                .andExpect(jsonPath("$.placa", is("ABC1D23")))
                .andExpect(jsonPath("$.chassi", is("9BWZZZ377VT004251")))
                .andExpect(jsonPath("$.renavam", is("82106426707")))
                .andExpect(jsonPath("$.marca", is("Toyota")))
                .andExpect(jsonPath("$.modelo", is("Corolla")))
                .andExpect(jsonPath("$.anoFabricacao", is(2020)))
                .andExpect(jsonPath("$.anoModelo", is(2021)))
                .andExpect(jsonPath("$.cor", is("Preto")))
                .andExpect(jsonPath("$.quilometragem", is(45000)))
                .andExpect(jsonPath("$.tipoCombustivel", is("DIESEL")))
                .andExpect(jsonPath("$.categoriaVeiculo", is("CARRO")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoBuscarVeiculoInexistentePorId() throws Exception {
        mockMvc.perform(get("/v1/veiculos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarVeiculoPorPlacaComSucesso() throws Exception {
        repository.save(veiculo);
        mockMvc.perform(get("/v1/veiculos/placa/{placa}", "ABC1D23")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(veiculo.getId().intValue())))
                .andExpect(jsonPath("$.placa", is("ABC1D23")))
                .andExpect(jsonPath("$.chassi", is("9BWZZZ377VT004251")))
                .andExpect(jsonPath("$.renavam", is("82106426707")))
                .andExpect(jsonPath("$.marca", is("Toyota")))
                .andExpect(jsonPath("$.modelo", is("Corolla")))
                .andExpect(jsonPath("$.anoFabricacao", is(2020)))
                .andExpect(jsonPath("$.anoModelo", is(2021)))
                .andExpect(jsonPath("$.cor", is("Preto")))
                .andExpect(jsonPath("$.quilometragem", is(45000)))
                .andExpect(jsonPath("$.tipoCombustivel", is("DIESEL")))
                .andExpect(jsonPath("$.categoriaVeiculo", is("CARRO")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoBuscarVeiculoInexistentePorPlaca() throws Exception {
        mockMvc.perform(get("/v1/veiculos/placa/{placa}", "XDXDXD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarVeiculoComSucesso() throws Exception {
        VeiculoRequestDTO veiculoValido = new VeiculoRequestDTO(
                "ABC1D23",
                "9BWZZZ377VT004251",
                "82106426707",
                "Toyota",
                "Corolla",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO);
        String json = objectMapper.writeValueAsString(veiculoValido);
        mockMvc.perform(post("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id",notNullValue()))
                .andExpect(jsonPath("$.placa", is("ABC1D23")))
                .andExpect(jsonPath("$.chassi", is("9BWZZZ377VT004251")))
                .andExpect(jsonPath("$.renavam", is("82106426707")))
                .andExpect(jsonPath("$.marca", is("Toyota")))
                .andExpect(jsonPath("$.modelo", is("Corolla")))
                .andExpect(jsonPath("$.anoFabricacao", is(2020)))
                .andExpect(jsonPath("$.anoModelo", is(2021)))
                .andExpect(jsonPath("$.cor", is("Preto")))
                .andExpect(jsonPath("$.quilometragem", is(45000)))
                .andExpect(jsonPath("$.tipoCombustivel", is("DIESEL")))
                .andExpect(jsonPath("$.categoriaVeiculo", is("CARRO")));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarVeiculoComDadosInvalidos() throws Exception {
        VeiculoRequestDTO veiculoInvalido = new VeiculoRequestDTO(
                "",// Placa Inválida
                "9BWZZZ377VT004251",
                "82106426707",
                "Toyota",
                "Corolla",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO);
        String json = objectMapper.writeValueAsString(veiculoInvalido);

        mockMvc.perform(post("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoCriarVeiculoComPlacaJaCadastrado() throws Exception {
        repository.save(veiculo);

        VeiculoRequestDTO veiculoConflito = new VeiculoRequestDTO(
                "ABC1D23",// Mesma Placa
                "3FAHP0HA6AR298374",
                "17059907791",
                "Ford",
                "Bronco",
                2020,
                2021,
                "Vermelha",
                null,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO);
        String json = objectMapper.writeValueAsString(veiculoConflito);
        mockMvc.perform(post("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoCriarVeiculoComChassiJaCadastrado() throws Exception {
        repository.save(veiculo);

        VeiculoRequestDTO veiculoConflito = new VeiculoRequestDTO(
                "ABC1D25",
                "9BWZZZ377VT004251",// Mesmo Chassi
                "17059907791",
                "Ford",
                "Bronco",
                2020,
                2021,
                "Vermelha",
                null,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO);
        String json = objectMapper.writeValueAsString(veiculoConflito);
        mockMvc.perform(post("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoCriarVeiculoComRenavamJaCadastrado() throws Exception {
        repository.save(veiculo);

        VeiculoRequestDTO veiculoConflito = new VeiculoRequestDTO(
                "ABC1D25",
                "3FAHP0HA6AR298374",
                "82106426707", // Mesmo Renavam
                "Ford",
                "Bronco",
                2020,
                2021,
                "Vermelha",
                null,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO);
        String json = objectMapper.writeValueAsString(veiculoConflito);
        mockMvc.perform(post("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarVeiculoComPlacaInvalida() throws Exception {
        VeiculoRequestDTO veiculoInvalido = new VeiculoRequestDTO(
                "",// Placa Inválida
                "9BWZZZ377VT004251",
                "82106426707",
                "Toyota",
                "Corolla",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO);
        String json = objectMapper.writeValueAsString(veiculoInvalido);

        mockMvc.perform(post("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarVeiculoComChassiInvalido() throws Exception {
        VeiculoRequestDTO veiculoInvalido = new VeiculoRequestDTO(
                "ABC1D23",
                "xyz", // Chassi Inválido
                "82106426707",
                "Toyota",
                "Corolla",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO);
        String json = objectMapper.writeValueAsString(veiculoInvalido);

        mockMvc.perform(post("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarVeiculoComRenavamInvalido() throws Exception {
        VeiculoRequestDTO veiculoInvalido = new VeiculoRequestDTO(
                "ABC1D23",
                "9BWZZZ377VT004251",
                "xyz", // Renavam Inválido
                "Toyota",
                "Corolla",
                2020,
                2021,
                "Preto",
                45000L,
                TipoCombustivel.DIESEL,
                CategoriaVeiculo.CARRO);
        String json = objectMapper.writeValueAsString(veiculoInvalido);

        mockMvc.perform(post("/v1/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAtualizarVeiculoComSucesso() throws Exception {
        VeiculoJpaEntity veiculoSalvo = repository.save(veiculo);

        VeiculoRequestDTO veiculoUpdate = new VeiculoRequestDTO(
                veiculoSalvo.getPlaca(),
                veiculoSalvo.getChassi(),
                veiculoSalvo.getRenavam(),
                veiculoSalvo.getMarca(),
                veiculoSalvo.getModelo(),
                veiculoSalvo.getAnoFabricacao(),
                veiculoSalvo.getAnoModelo(),
                "Rosa",
                0L,
                veiculoSalvo.getTipoCombustivel(),
                veiculoSalvo.getCategoriaVeiculo());
        String json = objectMapper.writeValueAsString(veiculoUpdate);


        mockMvc.perform(put("/v1/veiculos/{id}", veiculoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cor", is("Rosa")))
                .andExpect(jsonPath("$.quilometragem", is(0)))
                .andExpect(jsonPath("$.placa", is("ABC1D23")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoAtualizarVeiculoInexistente() throws Exception {
        VeiculoJpaEntity veiculoSalvo = repository.save(veiculo);

        VeiculoRequestDTO veiculoUpdate = new VeiculoRequestDTO(
                veiculoSalvo.getPlaca(),
                veiculoSalvo.getChassi(),
                veiculoSalvo.getRenavam(),
                veiculoSalvo.getMarca(),
                veiculoSalvo.getModelo(),
                veiculoSalvo.getAnoFabricacao(),
                veiculoSalvo.getAnoModelo(),
                "Rosa",
                0L,
                veiculoSalvo.getTipoCombustivel(),
                veiculoSalvo.getCategoriaVeiculo());
        String json = objectMapper.writeValueAsString(veiculoUpdate);

        mockMvc.perform(put("/v1/veiculos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoAtualizarPlacaDoVeiculoComOutroVeiculoComPlacaJaEmUso() throws Exception {
        VeiculoJpaEntity veiculoSalvo = repository.save(veiculo);
        repository.save(veiculoSegundo);

        VeiculoRequestDTO veiculoUpdate = new VeiculoRequestDTO(
                "ABC1D26",
                veiculoSalvo.getChassi(),
                veiculoSalvo.getRenavam(),
                veiculoSalvo.getMarca(),
                veiculoSalvo.getModelo(),
                veiculoSalvo.getAnoFabricacao(),
                veiculoSalvo.getAnoModelo(),
                veiculoSalvo.getCor(),
                veiculoSalvo.getQuilometragem(),
                veiculoSalvo.getTipoCombustivel(),
                veiculoSalvo.getCategoriaVeiculo());
        String json = objectMapper.writeValueAsString(veiculoUpdate);


        mockMvc.perform(put("/v1/veiculos/{id}", veiculoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoAtualizarChassiDoVeiculoComOutroVeiculoComChassiJaEmUso() throws Exception {
        VeiculoJpaEntity veiculoSalvo = repository.save(veiculo);
        repository.save(veiculoSegundo);

        VeiculoRequestDTO veiculoUpdate = new VeiculoRequestDTO(
                veiculoSalvo.getPlaca(),
                "8BWZZZ377VT004251",
                veiculoSalvo.getRenavam(),
                veiculoSalvo.getMarca(),
                veiculoSalvo.getModelo(),
                veiculoSalvo.getAnoFabricacao(),
                veiculoSalvo.getAnoModelo(),
                veiculoSalvo.getCor(),
                veiculoSalvo.getQuilometragem(),
                veiculoSalvo.getTipoCombustivel(),
                veiculoSalvo.getCategoriaVeiculo());
        String json = objectMapper.writeValueAsString(veiculoUpdate);


        mockMvc.perform(put("/v1/veiculos/{id}", veiculoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoAtualizarRenavamDoVeiculoComOutroVeiculoComRenavamJaEmUso() throws Exception {
        VeiculoJpaEntity veiculoSalvo = repository.save(veiculo);
        repository.save(veiculoSegundo);
        VeiculoRequestDTO veiculoUpdate = new VeiculoRequestDTO(
                veiculoSalvo.getPlaca(),
                veiculoSalvo.getChassi(),
                "23022215548",
                veiculoSalvo.getMarca(),
                veiculoSalvo.getModelo(),
                veiculoSalvo.getAnoFabricacao(),
                veiculoSalvo.getAnoModelo(),
                veiculoSalvo.getCor(),
                veiculoSalvo.getQuilometragem(),
                veiculoSalvo.getTipoCombustivel(),
                veiculoSalvo.getCategoriaVeiculo());
        String json = objectMapper.writeValueAsString(veiculoUpdate);

        mockMvc.perform(put("/v1/veiculos/{id}", veiculoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveDeletarVeiculoComSucesso() throws Exception {
        VeiculoJpaEntity veiculoSalvo = repository.save(veiculo);

        mockMvc.perform(delete("/v1/veiculos/{id}", veiculoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/veiculos/{id}", veiculoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoDeletarVeiculoInexistente() throws Exception {
        mockMvc.perform(delete("/v1/veiculos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}