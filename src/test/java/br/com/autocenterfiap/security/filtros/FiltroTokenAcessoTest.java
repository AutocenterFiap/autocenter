package br.com.autocenterfiap.security.filtros;

import br.com.autocenterfiap.security.repository.UsuarioRepository;
import br.com.autocenterfiap.security.repository.entity.Usuario;
import br.com.autocenterfiap.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FiltroTokenAcessoTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private FiltroTokenAcesso filtroTokenAcesso;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setNome("marcos");
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve autenticar usuário quando token válido é fornecido")
    void deveAutenticarUsuarioComTokenValido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenValido");
        when(tokenService.verificarToken("tokenValido")).thenReturn("marcos");
        when(usuarioRepository.findByNomeWithPerfis("marcos")).thenReturn(Optional.of(usuario));

        filtroTokenAcesso.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(usuario, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar quando não há header Authorization")
    void naoDeveAutenticarSemHeaderAuthorization() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filtroTokenAcesso.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoUsuarioNaoEncontrado() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenValido");
        when(tokenService.verificarToken("tokenValido")).thenReturn("marcos");
        when(usuarioRepository.findByNomeWithPerfis("marcos")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () ->
                filtroTokenAcesso.doFilterInternal(request, response, filterChain)
        );
    }

    @Test
    @DisplayName("Deve continuar filtro mesmo com token inválido")
    void deveContinuarFiltroComTokenInvalido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenInvalido");
        when(tokenService.verificarToken("tokenInvalido")).thenThrow(new RuntimeException("Token inválido"));

        assertThrows(RuntimeException.class, () ->
                filtroTokenAcesso.doFilterInternal(request, response, filterChain)
        );
    }
}
