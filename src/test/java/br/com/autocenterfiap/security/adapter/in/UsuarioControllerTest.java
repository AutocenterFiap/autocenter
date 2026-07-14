package br.com.autocenterfiap.security.adapter.in;

import br.com.autocenterfiap.security.adapter.in.request.AlteracaoSenhaRequest;
import br.com.autocenterfiap.security.adapter.in.request.PerfilRequest;
import br.com.autocenterfiap.security.adapter.in.response.PerfilResponse;
import br.com.autocenterfiap.security.application.usecase.AlterarSenhaUseCase;
import br.com.autocenterfiap.security.application.usecase.BuscarUsuarioPorNomeUseCase;
import br.com.autocenterfiap.security.application.usecase.SalvarUsuarioUseCase;
import br.com.autocenterfiap.security.adapter.mapper.UsuarioMapper;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
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

import static br.com.autocenterfiap.util.PerfilMockUtil.*;
import static br.com.autocenterfiap.util.UsuarioMockUtil.*;
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
    private BuscarUsuarioPorNomeUseCase buscarUsuarioPorNomeUseCase;

    @MockBean
    private SalvarUsuarioUseCase salvarUsuarioUseCase;

    @MockBean
    private AlterarSenhaUseCase alterarSenhaUseCase;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private List<PerfilJpaEntity> perfisMock;
    private List<PerfilResponse> perfisResponseMock;
    private List<PerfilRequest> perfisRequestMock;


    @BeforeEach
    void setUp() {
        perfisMock = createPerfisMock();
        perfisResponseMock = createPerfisResponseMock();
        perfisRequestMock = createPerfisRequestMock();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveObterUsuarioPorNome() throws Exception {
        var usuario = createUsuarioMock(1L, "joao", "senha123", perfisMock);
        var usuarioResponse = createUsuarioResponseMock(1L, "joao", perfisResponseMock);
        when(buscarUsuarioPorNomeUseCase.executar("joao")).thenReturn(usuario);
        when(usuarioMapper.toUsuarioResponse(usuario)).thenReturn(usuarioResponse);

        mockMvc.perform(get("/v1/usuarios/joao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("joao"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCadastrarUsuario() throws Exception {
        var usuarioRequest = createUsuarioRequestMock("maria", "senha123", perfisRequestMock);
        var usuario = createUsuarioMock(1l,"maria", "senha123", perfisMock);
        var usuarioSalvo =  createUsuarioMock(1l,"maria", "senha123", perfisMock);
        var usuarioResponse = createUsuarioResponseMock(1l,"maria", perfisResponseMock);

        when(usuarioMapper.toUsuario(usuarioRequest)).thenReturn(usuario);
        when(salvarUsuarioUseCase.executar(usuario)).thenReturn(usuarioSalvo);
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
        var usuario = createUsuarioMock(1l, "joao", "novaSenha", perfisMock);
        var usuarioAtualizado = createUsuarioMock(1l, "joao", "novaSenha", perfisMock);
        var usuarioResponse = createUsuarioResponseMock(1l,"joao", perfisResponseMock);

        when(usuarioMapper.toUsuario(alteracaoRequest)).thenReturn(usuario);
        when(alterarSenhaUseCase.executar(usuario)).thenReturn(usuarioAtualizado);
        when(usuarioMapper.toUsuarioResponse(usuarioAtualizado)).thenReturn(usuarioResponse);

        mockMvc.perform(patch("/v1/usuarios/senha/alteracao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"joao\",\"novaSenha\":\"novaSenha\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("joao"));
    }

}

