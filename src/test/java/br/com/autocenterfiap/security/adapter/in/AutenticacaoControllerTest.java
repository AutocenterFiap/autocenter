package br.com.autocenterfiap.security.adapter.in;

import br.com.autocenterfiap.security.adapter.in.request.PerfilRequest;
import br.com.autocenterfiap.security.adapter.in.response.PerfilResponse;
import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.application.usecase.BuscarUsuarioComPerfisUseCase;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import br.com.autocenterfiap.security.application.port.TokenPort;
import br.com.autocenterfiap.security.application.usecase.BuscarUsuarioPorNomeUseCase;
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
    private BuscarUsuarioComPerfisUseCase buscarUsuarioComPerfisUseCase;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenPort tokenPort;

    @MockBean
    private BuscarUsuarioPorNomeUseCase buscarUsuarioPorNomeUseCase;

    private List<PerfilJpaEntity> perfisMock;
    private List<PerfilResponse> perfisResponseMock;
    private List<PerfilRequest> perfisRequestMock;

    @BeforeEach
    void setUp() {
        perfisMock = getPerfisMock();
    }


    @Test
    void deveEfetuarLoginERetornarToken() throws Exception {
        var usuario = new UsuarioJpaEntity(1L, "joao", "senha123", perfisMock);
        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, usuario.getSenha());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(tokenPort.gerarToken(usuario.getUsername())).thenReturn("token-acesso");
        when(tokenPort.gerarRefreshToken(usuario.getUsername())).thenReturn("token-refresh");

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
        var usuario = new UsuarioJpaEntity(1L, "joao", "senha123", perfisMock);

        when(tokenPort.verificarToken("token-refresh")).thenReturn("maria");
        when(buscarUsuarioPorNomeUseCase.executar("maria")).thenReturn(usuario);
        when(tokenPort.gerarToken(usuario.getUsername())).thenReturn("novo-token-acesso");
        when(tokenPort.gerarRefreshToken(usuario.getUsername())).thenReturn("novo-token-refresh");

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

    private List<PerfilJpaEntity> getPerfisMock() {
        return List.of(new PerfilJpaEntity(1L, PerfilType.ADMIN, null));
    }
}
