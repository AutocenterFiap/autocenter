package br.com.autocenterfiap.security.controller;

import br.com.autocenterfiap.security.controller.request.PerfilRequest;
import br.com.autocenterfiap.security.controller.response.PerfilResponse;
import br.com.autocenterfiap.security.enums.PerfilType;
import br.com.autocenterfiap.security.repository.UsuarioRepository;
import br.com.autocenterfiap.security.repository.entity.Perfil;
import br.com.autocenterfiap.security.repository.entity.Usuario;
import br.com.autocenterfiap.security.service.TokenService;
import br.com.autocenterfiap.security.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutenticacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioService usuarioService;

    private List<Perfil> perfisMock;
    private List<PerfilResponse> perfisResponseMock;
    private List<PerfilRequest> perfisRequestMock;

    @BeforeEach
    void setUp() {
        perfisMock = getPerfisMock();
    }


    @Test
    void deveEfetuarLoginERetornarToken() throws Exception {
        var usuario = new Usuario(1L, "joao", "senha123", perfisMock);
        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, usuario.getSenha());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(tokenService.gerarToken(usuario)).thenReturn("token-acesso");
        when(tokenService.gerarRefreshToken(usuario)).thenReturn("token-refresh");

        String json = """
            { "nome": "ADMIN", "senha": "ADMIN" }
            """;

        mockMvc.perform(post("/v1/oauth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenAcesso").value("token-acesso"))
                .andExpect(jsonPath("$.refreshToken").value("token-refresh"));
    }

    @Test
    void deveAtualizarTokenERetornarNovoToken() throws Exception {
        var usuario = new Usuario(1L, "joao", "senha123", perfisMock);

        when(tokenService.verificarToken("token-refresh")).thenReturn("maria");
        when(usuarioService.findByNome("maria")).thenReturn(usuario);
        when(tokenService.gerarToken(usuario)).thenReturn("novo-token-acesso");
        when(tokenService.gerarRefreshToken(usuario)).thenReturn("novo-token-refresh");

        String json = """
            { "refreshToken": "token-refresh" }
            """;

        mockMvc.perform(post("/v1/oauth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenAcesso").value("novo-token-acesso"))
                .andExpect(jsonPath("$.refreshToken").value("novo-token-refresh"));
    }

    private List<Perfil> getPerfisMock() {
        return List.of(new Perfil(1L, PerfilType.ADMIN, null));
    }
}
