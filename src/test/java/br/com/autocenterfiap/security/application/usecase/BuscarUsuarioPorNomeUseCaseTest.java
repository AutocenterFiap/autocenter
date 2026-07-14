package br.com.autocenterfiap.security.application.usecase;

import br.com.autocenterfiap.security.application.port.UsuarioRepositoryPort;
import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.domain.exception.UsuarioNaoEncontradoException;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.autocenterfiap.util.PerfilMockUtil.createPerfisMock;
import static br.com.autocenterfiap.util.UsuarioMockUtil.createUsuarioMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarUsuarioPorNomeUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private BuscarUsuarioPorNomeUseCase buscarUsuarioPorNomeUseCase;

    private UsuarioJpaEntity usuario;

    @BeforeEach
    void setUp() {
        usuario = createUsuarioMock(1L, "maria", "senha123", createPerfisMock(PerfilType.ADMIN));
    }

    @Test
    void deveEncontrarUsuarioPorNome() {
        when(usuarioRepositoryPort.buscarPorNome("maria")).thenReturn(Optional.of(usuario));

        UsuarioJpaEntity encontrado = buscarUsuarioPorNomeUseCase.executar("maria");

        assertEquals("maria", encontrado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepositoryPort.buscarPorNome("maria")).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> buscarUsuarioPorNomeUseCase.executar("maria"));
    }
}
