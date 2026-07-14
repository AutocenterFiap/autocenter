package br.com.autocenterfiap.security.infrastructure.security;

import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfisMock;
import static br.com.autocenterfiap.util.UsuarioMockUtil.createUsuarioMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    private UsuarioJpaEntity usuario;

    @BeforeEach
    void setUp() {
        usuario = createUsuarioMock(1L, "maria", "senha123", createPerfisMock(PerfilType.ADMIN));
    }

    @Test
    void deveCarregarUsuarioPorNome() {
        when(usuarioRepositoryPort.buscarPorNome("maria")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername("maria");

        assertEquals("maria", userDetails.getUsername());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepositoryPort.buscarPorNome("maria")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioDetailsService.loadUserByUsername("maria"));
    }
}
