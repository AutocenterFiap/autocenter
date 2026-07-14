package br.com.autocenterfiap.security.application.usecase;

import br.com.autocenterfiap.security.application.port.PerfilRepositoryPort;
import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.domain.exception.PerfilNaoEncontradoException;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfilMock;
import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfisMock;
import static br.com.autocenterfiap.util.UsuarioMockUtil.createUsuarioMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalvarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PerfilRepositoryPort perfilRepositoryPort;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private SalvarUsuarioUseCase salvarUsuarioUseCase;

    private UsuarioJpaEntity usuario;
    private PerfilJpaEntity perfilMock;

    @BeforeEach
    void setUp() {
        perfilMock = createPerfilMock(PerfilType.ADMIN);
        usuario = createUsuarioMock(1L, "maria", "senha123", createPerfisMock(PerfilType.ADMIN));
    }

    @Test
    void deveSalvarUsuarioComSenhaCodificadaEPerfisGerenciados() {
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(perfilRepositoryPort.buscarPorNome(PerfilType.ADMIN)).thenReturn(Optional.of(perfilMock));
        when(usuarioRepositoryPort.salvar(any(UsuarioJpaEntity.class))).thenReturn(usuario);

        UsuarioJpaEntity salvo = salvarUsuarioUseCase.executar(usuario);

        assertEquals("senhaCodificada", salvo.getSenha());
        assertEquals(PerfilType.ADMIN, salvo.getPerfis().get(0).getNome());
        verify(usuarioRepositoryPort, times(1)).salvar(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoPerfilNaoEncontrado() {
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(perfilRepositoryPort.buscarPorNome(PerfilType.ADMIN)).thenReturn(Optional.empty());

        assertThrows(PerfilNaoEncontradoException.class, () -> salvarUsuarioUseCase.executar(usuario));
    }
}
