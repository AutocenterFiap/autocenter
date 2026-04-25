package br.com.autocenterfiap.security.service;

import br.com.autocenterfiap.security.repository.entity.Perfil;
import br.com.autocenterfiap.security.repository.entity.Usuario;
import br.com.autocenterfiap.security.enums.PerfilType;
import br.com.autocenterfiap.security.exception.PerfilNaoEncontradoException;
import br.com.autocenterfiap.security.exception.UsuarioNaoEncontradoException;
import br.com.autocenterfiap.security.repository.PerfilRepository;
import br.com.autocenterfiap.security.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfilMock;
import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfisMock;
import static br.com.autocenterfiap.util.UsuarioMockUtil.createUsuarioMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    private Perfil perfilMock;

    @BeforeEach
    void setUp() {
        perfilMock = createPerfilMock(PerfilType.ADMIN);
        usuario = createUsuarioMock(1L, "maria", "senha123", createPerfisMock(PerfilType.ADMIN));
    }

    @Test
    void deveSalvarUsuarioComSenhaCodificadaEPerfisGerenciados() {
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(perfilRepository.findByNome(PerfilType.ADMIN)).thenReturn(Optional.of(perfilMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario salvo = usuarioService.salvar(usuario);

        assertEquals("senhaCodificada", salvo.getSenha());
        assertEquals(PerfilType.ADMIN, salvo.getPerfis().get(0).getNome());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoPerfilNaoEncontrado() {
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(perfilRepository.findByNome(PerfilType.ADMIN)).thenReturn(Optional.empty());

        assertThrows(PerfilNaoEncontradoException.class, () -> usuarioService.salvar(usuario));
    }

    @Test
    void deveCarregarUsuarioPorNome() {
        when(usuarioRepository.findByNome("maria")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = usuarioService.loadUserByUsername("maria");

        assertEquals("maria", userDetails.getUsername());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoNoLoadUserByUsername() {
        when(usuarioRepository.findByNome("maria")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername("maria"));
    }

    @Test
    void deveEncontrarUsuarioPorNome() {
        when(usuarioRepository.findByNome("maria")).thenReturn(Optional.of(usuario));

        Usuario encontrado = usuarioService.findByNome("maria");

        assertEquals("maria", encontrado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoNoFindByNome() {
        when(usuarioRepository.findByNome("maria")).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.findByNome("maria"));
    }

    @Test
    void deveAlterarSenhaDoUsuario() {
        Usuario usuarioAlteracao = new Usuario();
        usuarioAlteracao.setNome("maria");
        usuarioAlteracao.setSenha("novaSenha");

        when(usuarioRepository.findByNome("maria")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAlteracao);

        Usuario atualizado = usuarioService.alterarSenha(usuarioAlteracao);

        assertEquals("novaSenha", atualizado.getSenha());
        verify(usuarioRepository, times(1)).save(usuarioAlteracao);
    }
}
