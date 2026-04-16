package br.com.autocenterfiap.security.controller;

import br.com.autocenterfiap.security.entity.Perfil;
import br.com.autocenterfiap.security.entity.Usuario;
import br.com.autocenterfiap.security.enums.PerfilType;
import br.com.autocenterfiap.security.mapper.UsuarioMapper;
import br.com.autocenterfiap.security.model.*;
import br.com.autocenterfiap.security.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioMapper usuarioMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private List<Perfil> perfisMock;
    private List<PerfilResponse> perfisResponseMock;
    private List<PerfilRequest> perfisRequestMock;


    @BeforeEach
    void setUp() {
        perfisMock = getPerfisMock();
        perfisResponseMock = getPerfisResponseMock();
        perfisRequestMock = getPerfisRequestMock();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveObterUsuarioPorNome() throws Exception {
        var usuario = new Usuario(1L, "joao", "senha123", perfisMock);
        var usuarioResponse = new UsuarioResponse(1L, "joao", perfisResponseMock);
        when(usuarioService.findByNome("joao")).thenReturn(usuario);
        when(usuarioMapper.toUsuarioResponse(usuario)).thenReturn(usuarioResponse);

        mockMvc.perform(get("/v1/usuarios/joao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("joao"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCadastrarUsuario() throws Exception {
        var usuarioRequest = new UsuarioRequest("maria", "senha123", perfisRequestMock);
        var usuario = new Usuario(1l,"maria", "senha123", perfisMock);
        var usuarioSalvo =  new Usuario(1l,"maria", "senha123", perfisMock);
        var usuarioResponse = new UsuarioResponse(1l,"maria", perfisResponseMock);

        when(usuarioMapper.toUsuario(usuarioRequest)).thenReturn(usuario);
        when(usuarioService.salvar(usuario)).thenReturn(usuarioSalvo);
        when(usuarioMapper.toUsuarioResponse(usuarioSalvo)).thenReturn(usuarioResponse);

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"maria\",\"senha\":\"senha123\", \"perfis\":[{\"nome\":\"ADMIN\"}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("maria"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAlterarSenha() throws Exception {
        var alteracaoRequest = new AlteracaoSenhaRequest("joao", "novaSenha");
        var usuario = new Usuario(1l, "joao", "novaSenha", perfisMock);
        var usuarioAtualizado = new Usuario(1l, "joao", "novaSenha", perfisMock);
        var usuarioResponse = new UsuarioResponse(1l,"joao", perfisResponseMock);

        when(usuarioMapper.toUsuario(alteracaoRequest)).thenReturn(usuario);
        when(usuarioService.alterarSenha(usuario)).thenReturn(usuarioAtualizado);
        when(usuarioMapper.toUsuarioResponse(usuarioAtualizado)).thenReturn(usuarioResponse);

        mockMvc.perform(patch("/v1/usuarios/senha/alteracao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"joao\",\"novaSenha\":\"novaSenha\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("joao"));
    }

    private List<Perfil> getPerfisMock() {
        return List.of(new Perfil(1L, PerfilType.ADMIN, null));
    }

    private List<PerfilRequest> getPerfisRequestMock() {
        return List.of(new PerfilRequest(PerfilType.ADMIN));
    }

    private List<PerfilResponse> getPerfisResponseMock() {
        return List.of(new PerfilResponse(PerfilType.ADMIN));
    }
}

