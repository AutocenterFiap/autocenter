package br.com.autocenterfiap.cliente.controller;

import br.com.autocenterfiap.cliente.enums.TipoCliente;
import br.com.autocenterfiap.cliente.model.Cliente;
import br.com.autocenterfiap.cliente.model.Endereco;
import br.com.autocenterfiap.cliente.repository.ClienteRepository;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("ClienteController - Testes de Integração")
class ClienteControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clientePF;
    private Cliente clientePJ;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();

        // Setup Endereço
        endereco = new Endereco(
                "01310100",
                "Avenida Paulista",
                "1578",
                "Andar 5",
                "Bela Vista",
                "São Paulo",
                "SP"
        );

        // Setup Cliente Pessoa Física
        clientePF = new Cliente();
        clientePF.setNome("João da Silva");
        clientePF.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clientePF.setDocumento("11144477735");
        clientePF.setEmail("joao.silva@email.com");
        clientePF.setTelefone("11987654321");
        clientePF.setEndereco(endereco);
        clientePF.setDataNascimento(LocalDate.of(1990, 5, 15));

        // Setup Cliente Pessoa Jurídica
        clientePJ = new Cliente();
        clientePJ.setNome("Empresa ABC LTDA");
        clientePJ.setTipoCliente(TipoCliente.PESSOA_JURIDICA);
        clientePJ.setDocumento("11222333000181");
        clientePJ.setEmail("contato@empresaabc.com");
        clientePJ.setTelefone("1133334444");
        clientePJ.setEndereco(endereco);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarTodosOsClientes() throws Exception {
        clienteRepository.save(clientePF);
        clienteRepository.save(clientePJ);

        mockMvc.perform(get("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", notNullValue()))
                .andExpect(jsonPath("$[1].nome", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornarListaVaziaQuandoNaoHouverClientes() throws Exception {
        mockMvc.perform(get("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarClientePorIdComSucesso() throws Exception {
        Cliente clienteSalvo = clienteRepository.save(clientePF);

        mockMvc.perform(get("/v1/api/clientes/{id}", clienteSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clienteSalvo.getId().intValue())))
                .andExpect(jsonPath("$.nome", is("João da Silva")))
                .andExpect(jsonPath("$.documento", is("11144477735")))
                .andExpect(jsonPath("$.tipoCliente", is("PESSOA_FISICA")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoBuscarClienteInexistentePorId() throws Exception {
        mockMvc.perform(get("/v1/api/clientes/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarClientePorDocumentoComSucesso() throws Exception {
        clienteRepository.save(clientePF);

        mockMvc.perform(get("/v1/api/clientes/documento/{documento}", "11144477735")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("João da Silva")))
                .andExpect(jsonPath("$.documento", is("11144477735")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoBuscarClienteInexistentePorDocumento() throws Exception {
        mockMvc.perform(get("/v1/api/clientes/documento/{documento}", "99999999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarClientePessoaFisicaComSucesso() throws Exception {
        mockMvc.perform(post("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientePF)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("João da Silva")))
                .andExpect(jsonPath("$.documento", is("11144477735")))
                .andExpect(jsonPath("$.tipoCliente", is("PESSOA_FISICA")))
                .andExpect(jsonPath("$.email", is("joao.silva@email.com")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarClientePessoaJuridicaComSucesso() throws Exception {
        mockMvc.perform(post("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientePJ)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Empresa ABC LTDA")))
                .andExpect(jsonPath("$.documento", is("11222333000181")))
                .andExpect(jsonPath("$.tipoCliente", is("PESSOA_JURIDICA")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarClienteComDadosInvalidos() throws Exception {
        Cliente clienteInvalido = new Cliente();
        clienteInvalido.setNome(""); // nome vazio - inválido
        clienteInvalido.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteInvalido.setDocumento("11144477735");

        mockMvc.perform(post("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoCriarClienteComDocumentoJaCadastrado() throws Exception {
        clienteRepository.save(clientePF);

        Cliente clienteDuplicado = new Cliente();
        clienteDuplicado.setNome("Maria da Silva");
        clienteDuplicado.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteDuplicado.setDocumento("11144477735"); // mesmo documento
        clienteDuplicado.setEmail("maria@email.com");
        clienteDuplicado.setTelefone("11888887777");
        clienteDuplicado.setEndereco(endereco);

        mockMvc.perform(post("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDuplicado)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar409AoCriarClienteComEmailJaCadastrado() throws Exception {
        clienteRepository.save(clientePF);

        Cliente clienteDuplicado = new Cliente();
        clienteDuplicado.setNome("Maria da Silva");
        clienteDuplicado.setTipoCliente(TipoCliente.PESSOA_FISICA);
        clienteDuplicado.setDocumento("52998224725");
        clienteDuplicado.setEmail("joao.silva@email.com"); // mesmo email
        clienteDuplicado.setTelefone("11888887777");
        clienteDuplicado.setEndereco(endereco);

        mockMvc.perform(post("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDuplicado)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarClientePFComCpfInvalido() throws Exception {
        clientePF.setDocumento("12345678901"); // CPF inválido

        mockMvc.perform(post("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientePF)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoCriarClientePJComCnpjInvalido() throws Exception {
        clientePJ.setDocumento("12345678901234"); // CNPJ inválido

        mockMvc.perform(post("/v1/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientePJ)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAtualizarClienteComSucesso() throws Exception {
        Cliente clienteSalvo = clienteRepository.save(clientePF);

        clienteSalvo.setNome("João da Silva Updated");
        clienteSalvo.setTelefone("11999998888");

        mockMvc.perform(put("/v1/api/clientes/{id}", clienteSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteSalvo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("João da Silva Updated")))
                .andExpect(jsonPath("$.telefone", is("11999998888")))
                .andExpect(jsonPath("$.documento", is("11144477735")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
        mockMvc.perform(put("/v1/api/clientes/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientePF)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400AoTentarAlterarDocumento() throws Exception {
        Cliente clienteSalvo = clienteRepository.save(clientePF);

        Cliente clienteComDocumentoAlterado = new Cliente();
        clienteComDocumentoAlterado.setId(clienteSalvo.getId());
        clienteComDocumentoAlterado.setNome(clienteSalvo.getNome());
        clienteComDocumentoAlterado.setTipoCliente(clienteSalvo.getTipoCliente());
        clienteComDocumentoAlterado.setDocumento("52998224725"); // documento diferente
        clienteComDocumentoAlterado.setEmail(clienteSalvo.getEmail());
        clienteComDocumentoAlterado.setTelefone(clienteSalvo.getTelefone());
        clienteComDocumentoAlterado.setEndereco(clienteSalvo.getEndereco());
        clienteComDocumentoAlterado.setDataNascimento(clienteSalvo.getDataNascimento());

        mockMvc.perform(put("/v1/api/clientes/{id}", clienteSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteComDocumentoAlterado)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveDeletarClienteComSucesso() throws Exception {
        Cliente clienteSalvo = clienteRepository.save(clientePF);

        mockMvc.perform(delete("/v1/api/clientes/{id}", clienteSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/api/clientes/{id}", clienteSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar404AoDeletarClienteInexistente() throws Exception {
        mockMvc.perform(delete("/v1/api/clientes/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
